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
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static org.pknu.weather.dto.converter.ExtraWeatherConverter.toExtraWeather;
import static org.pknu.weather.dto.converter.ExtraWeatherConverter.toExtraWeatherInfo;
import static org.pknu.weather.dto.converter.LocationConverter.toLocationDTO;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class WeatherService {
    private final WeatherFeignClientUtils weatherFeignClientUtils;
    private final WeatherRepository weatherRepository;
    private final ExtraWeatherRepository extraWeatherRepository;
    private final MemberRepository memberRepository;
    private final ExtraWeatherApiUtils extraWeatherApiUtils;
    private final LocationRepository locationRepository;
    private final JdbcTemplate jdbcTemplate;

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
     * @param locationId
     * @param newForecast 공공데이터 API에서 받아온 단기날씨예보 값 list
     */
    @Async("WeatherCUDExecutor")
    @Transactional
    public void saveWeathersAsync(Long locationId, List<Weather> newForecast) {
        Location location = locationRepository.safeFindById(locationId);

        List<Weather> weatherList = new ArrayList<>(newForecast).stream()
                .peek(weather -> weather.addLocation(location))
                .toList();

        weatherRepository.saveAll(weatherList);
    }

    /**
     * 날씨 정보를 저장합니다. 비동기적으로 동작합니다.
     *
     * @param locationId
     * @param newForecast 공공데이터 API에서 받아온 단기날씨예보 값 list
     */
    @Async("WeatherCUDExecutor")
    @Transactional
    public void bulkSaveWeathersAsync(Long locationId, List<Weather> newForecast) {
        Location location = locationRepository.safeFindById(locationId);
        String query =
                "INSERT INTO weather(basetime, location_id, wind_speed, humidity, rain_prob, rain, rain_type, temperature, sensible_temperature, snow_cover, sky_type, presentation_time) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        batchUpdateWeathers(query, newForecast, location);
    }

    /**
     * 단기 날씨 예보 API가 3시간 마다 갱신되기 때문에, 날씨 데이터 갱신을 위한 메서드
     *
     * @param locationId API를 호출한 사용자의 Location id
     * @return 해당 위치의 날씨 데이터 List
     */
    @Async("WeatherCUDExecutor")
    @Transactional
    public void updateWeathersAsync(Long locationId) {
        Location location = locationRepository.safeFindById(locationId);
        Map<LocalDateTime, Weather> oldWeatherMap = weatherRepository.findAllByLocationAfterNow(location);
        List<Weather> newWeatherList = weatherFeignClientUtils.getVillageShortTermForecast(location);
        List<Weather> weatherList = updateWeathers(oldWeatherMap, newWeatherList, location);
        weatherRepository.saveAll(weatherList);
    }

    @Async("WeatherCUDExecutor")
    @Transactional
    public void bulkUpdateWeathersAsync(Long locationId) {
        Location location = locationRepository.safeFindById(locationId);
        Map<LocalDateTime, Weather> oldWeatherMap = weatherRepository.findAllByLocationAfterNow(location);
        List<Weather> newWeatherList = weatherFeignClientUtils.getVillageShortTermForecast(location);
        List<Weather> weathersList = updateWeathers(oldWeatherMap, newWeatherList, location);
        String query =
                "INSERT INTO weather(basetime, location_id, wind_speed, humidity, rain_prob, rain, rain_type, temperature, sensible_temperature, snow_cover, sky_type, presentation_time) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) "
                        + "ON DUPLICATE KEY UPDATE "
                        + "wind_speed = VALUES(wind_speed), humidity = VALUES(humidity), rain_prob = VALUES(rain_prob), rain = VALUES(rain), rain_type = VALUES(rain_type), sensible_temperature = VALUES(sensible_temperature), snow_cover = VALUES(snow_cover), sky_type = VALUES(sky_type), presentation_time = VALUES(presentation_time)";
        batchUpdateWeathers(query, weathersList, location);
    }

    private List<Weather> updateWeathers(Map<LocalDateTime, Weather> oldWeatherMap, List<Weather> newWeatherList,
                                         Location location) {
        newWeatherList.forEach(newWeather -> {
            LocalDateTime presentationTime = newWeather.getPresentationTime();
            if (oldWeatherMap.containsKey(presentationTime)) {
                // 이미 존재하는 데이터 갱신
//                oldWeatherMap.put(presentationTime, newWeather);
                Weather oldWeather = oldWeatherMap.get(presentationTime);
                oldWeather.updateWeather(newWeather);
            } else {
                newWeather.addLocation(location); // 새 데이터만 추가
                oldWeatherMap.put(newWeather.getPresentationTime(), newWeather);
            }
        });

        return new ArrayList<>(oldWeatherMap.values());
    }

    private void batchUpdateWeathers(String query, List<Weather> forecast, Location location) {
        jdbcTemplate.batchUpdate(query,
                new BatchPreparedStatementSetter() {

                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        Weather w = forecast.get(i);
                        w.updateSensibleTemperature();

                        ps.setTimestamp(1, Timestamp.valueOf(w.getBasetime()));
                        ps.setLong(2, location.getId());
                        ps.setDouble(3, w.getWindSpeed());
                        ps.setInt(4, w.getHumidity());
                        ps.setInt(5, w.getRainProb());
                        ps.setFloat(6, w.getRain());
                        ps.setInt(7, w.getRainType().ordinal());
                        ps.setInt(8, w.getTemperature());
                        ps.setDouble(9, w.getSensibleTemperature());
                        ps.setFloat(10, w.getSnowCover());
                        ps.setInt(11, w.getSkyType().ordinal());
                        ps.setObject(12, w.getPresentationTime());
                    }

                    @Override
                    public int getBatchSize() {
                        log.debug("batch size : {}", forecast.size());
                        return forecast.size();
                    }
                });
    }

    /**
     * 예보 시간이 현재 보다 과거이면 모두 삭제합니다.v
     */
    @Async("WeatherCUDExecutor")
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

    /**
     * 단기 날씨 예보 API가 3시간 마다 갱신되 기 때문에, 날씨 데이터 갱신을 위한 메서드
     *
     * @param locationId API를 호출한 사용자의 Location id
     * @return 해당 위치의 날씨 데이터 List
     */
    public void updateWeathers(Long locationId) {

        Location location = locationRepository.safeFindById(locationId);
        Map<LocalDateTime, Weather> oldWeatherMap = weatherRepository.findAllByLocationAfterNow(location);

        List<Weather> weatherList = weatherFeignClientUtils.getVillageShortTermForecast(location).stream()
                .peek(newWeather -> {
                    LocalDateTime presentationTime = newWeather.getPresentationTime();
                    if (oldWeatherMap.containsKey(presentationTime)) {
                        Weather oldWeather = oldWeatherMap.get(presentationTime);
                        oldWeather.updateWeather(newWeather);
                    }
                    newWeather.addLocation(location);
                }).toList();

        weatherRepository.saveAll(weatherList);
    }
}
