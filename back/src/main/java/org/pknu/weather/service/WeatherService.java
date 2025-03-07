package org.pknu.weather.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pknu.weather.apiPayload.code.status.ErrorStatus;
import org.pknu.weather.domain.ExtraWeather;
import org.pknu.weather.domain.Location;
import org.pknu.weather.domain.Member;
import org.pknu.weather.domain.Weather;
import org.pknu.weather.dto.WeatherResponse;
import org.pknu.weather.dto.WeatherResponse.ExtraWeatherInfo;
import org.pknu.weather.exception.GeneralException;
import org.pknu.weather.feignClient.utils.ExtraWeatherApiUtils;
import org.pknu.weather.feignClient.utils.WeatherFeignClientUtils;
import org.pknu.weather.repository.ExtraWeatherRepository;
import org.pknu.weather.repository.LocationRepository;
import org.pknu.weather.repository.MemberRepository;
import org.pknu.weather.repository.WeatherRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.pknu.weather.dto.converter.ExtraWeatherConverter.toExtraWeather;
import static org.pknu.weather.dto.converter.ExtraWeatherConverter.toExtraWeatherInfo;
import static org.pknu.weather.dto.converter.LocationConverter.toLocationDTO;

@Service
@RequiredArgsConstructor
@Slf4j
public class WeatherService {
    private final WeatherFeignClientUtils weatherFeignClientUtils;
    private final WeatherRepository weatherRepository;
    private final ExtraWeatherRepository extraWeatherRepository;
    private final MemberRepository memberRepository;
    private final ExtraWeatherApiUtils extraWeatherApiUtils;
    private final LocationRepository locationRepository;
    private final WeatherWriteService weatherWriteService;

    /**
     * TODO: 성능 개선 필요
     * 현재 ~ +24시간 까지의 날씨 정보를 불러옵니다.
     *
     * @param location
     * @return
     */
    @Transactional(readOnly = true)
    public List<Weather> getWeathers(Location location) {
        return weatherRepository.findAllWithLocation(location.getId(), LocalDateTime.now().plusHours(24)).stream()
                .sorted(Comparator.comparing(Weather::getPresentationTime))
                .toList();
    }

    /**
     * 위도와 경도에 해당하는 지역(읍면동)의 24시간치 날씨 단기 예보 정보를 저장합니다.
     *
     * @return 위도와 경도에 해당하는 Location의 Weather list를 반환
     */
    @Transactional
    public List<Weather> saveWeathers(Location location) {
        List<Weather> values = weatherFeignClientUtils.getVillageShortTermForecast(location);
        List<Weather> weatherList = new ArrayList<>(values);

        weatherList.forEach(w -> w.addLocation(location));
        return weatherRepository.saveAll(weatherList);
    }

    /**
     * 날씨 정보를 저장합니다. 비동기적으로 동작합니다.
     *
     * @param location member.getLocation()
     * @param forecast 공공데이터 API에서 받아온 단기날씨예보 값 list
     */
    @Async("threadPoolTaskExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveWeathersAsync(Location location, List<Weather> forecast) {
        weatherWriteService.saveWeathersAsync(location, forecast);
    }

    /**
     * 단기 날씨 예보 API가 3시간 마다 갱신되기 때문에, 날씨 데이터 갱신을 위한 메서드
     *
     * @param locationId API를 호출한 사용자의 Location id
     * @return 해당 위치의 날씨 데이터 List
     */
    @Async("threadPoolTaskExecutor")
    public void updateWeathersAsync(Long locationId) {
        weatherWriteService.updateWeathersAsync(locationId);
    }

    /**
     * 예보 시간이 현재 보다 과거이면 모두 삭제합니다.v
     */
    @Async("threadPoolDeleteTaskExecutor")
    public void bulkDeletePastWeather() {
        weatherRepository.bulkDeletePastWeathers();
    }

    @Transactional
    public WeatherResponse.ExtraWeatherInfo extraWeatherInfo(String email, Long locationId) {

        Member member = memberRepository.findMemberWithLocationByEmail(email)
                .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));

        Location location = getLocation(member, locationId);

        return extraWeatherRepository.findByLocationId(location.getId())
                .map(extraWeather -> processExistingExtraWeather(location, extraWeather))
                .orElseGet(() -> fetchAndSaveExtraWeather(location));
    }

    private WeatherResponse.ExtraWeatherInfo processExistingExtraWeather(Location location, ExtraWeather extraWeather) {
        if (extraWeather.getBasetime().isBefore(LocalDateTime.now().minusHours(3))) {
            return updateAndReturnExtraWeatherInfo(location, extraWeather);
        }
        return toExtraWeatherInfo(extraWeather);
    }

    private ExtraWeatherInfo updateAndReturnExtraWeatherInfo(Location location, ExtraWeather extraWeather) {
        ExtraWeatherInfo extraWeatherInfo = extraWeatherApiUtils.getExtraWeatherInfo(
                toLocationDTO(location), extraWeather.getBasetime());
        extraWeather.updateExtraWeather(extraWeatherInfo);
        log.debug("기타 날씨 정보 업데이트 완료");
        return extraWeatherInfo;
    }

    private ExtraWeatherInfo fetchAndSaveExtraWeather(Location location) {
        ExtraWeatherInfo extraWeatherInfo = extraWeatherApiUtils.getExtraWeatherInfo(
                toLocationDTO(location));

        saveExtraWeatherInfo(location, extraWeatherInfo);
        log.debug("기타 날씨 정보 저장 완료");
        return extraWeatherInfo;
    }

    private Location getLocation(Member member, Long locationId) {
        if (locationId != null) {
            return locationRepository.safeFindById(locationId);
        }
        return member.getLocation();
    }

    private void saveExtraWeatherInfo(Location location, ExtraWeatherInfo extraWeatherInfo) {
        extraWeatherRepository.save(toExtraWeather(location, extraWeatherInfo));
        log.debug("기타 날씨 정보 저장 완료");
    }
}
