package org.pknu.weather.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pknu.weather.common.WeatherParamsFactory;
import org.pknu.weather.common.formatter.DateTimeFormatter;
import org.pknu.weather.common.utils.GeometryUtils;
import org.pknu.weather.domain.Location;
import org.pknu.weather.domain.Weather;
import org.pknu.weather.dto.WeatherApiResponse;
import org.pknu.weather.feignClient.WeatherFeignClient;
import org.pknu.weather.feignClient.dto.PointDTO;
import org.pknu.weather.feignClient.dto.WeatherParams;
import org.pknu.weather.feignClient.utils.WeatherApiUtils;
import org.pknu.weather.repository.LocationRepository;
import org.pknu.weather.repository.WeatherRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class WeatherService {
    private final WeatherFeignClient weatherFeignClient;
    private final WeatherRepository weatherRepository;
    private final LocationRepository locationRepository;

    @Value("${api.weather.service-key}")
    private String weatherServiceKey;

    /**
     * 사용자의 위도 경도 및 기타 정보를 받아와 Point(x, y)로 치환하고 weather로 반환한다.
     * @Location 사용자 위치 엔티티
     * @return now ~ 24 시간의 Wether 엔티티를 담고있는 List
     */
    private List<Weather> getPreprocessWeatherList(Location location) {
        float lon = location.getLongitude().floatValue();
        float lat = location.getLatitude().floatValue();

        PointDTO pointDTO = GeometryUtils.coordinateToPoint(lon, lat);
        String date = DateTimeFormatter.getFormattedDate();
        String time = DateTimeFormatter.getFormattedTimeByThreeHour();

        WeatherParams weatherParams = WeatherParamsFactory.create(weatherServiceKey, date, time, pointDTO);

        WeatherApiResponse weatherApiResponse = weatherFeignClient.getVillageShortTermForecast(weatherParams);
        List<WeatherApiResponse.Response.Body.Items.Item> itemList = weatherApiResponse.getResponse().getBody().getItems().getItemList();

        return WeatherApiUtils.responseProcess(itemList, date, time);
    }


    /**
     * TODO: 성능 개선 필요
     * 현재 ~ +24시간 까지의 날씨 정보를 불러옵니다.
     *
     * @param location
     * @return
     */
    public List<Weather> getWeathers(Location location) {
        return weatherRepository.findAllWithLocation(location, LocalDateTime.now().plusHours(24)).stream()
                .sorted(Comparator.comparing(Weather::getPresentationTime))
                .toList();
    }

    /**
     * 위도와 경도에 해당하는 지역(읍면동)의 24시간치 날씨 단기 예보 정보를 저장합니다.
     *
     * @return 위도와 경도에 해당하는 Location의 Weather list를 반환
     */
    // TODO: 비동기 처리
    @Transactional
    public List<Weather> saveWeathers(Location location) {
        List<Weather> values = getPreprocessWeatherList(location);
        List<Weather> weatherList = new ArrayList<>(values);

        weatherList.forEach(w -> w.addLocation(location));
        return weatherRepository.saveAll(weatherList);
    }

    /**
     * 단기 날씨 예보 API가 3시간 마다 갱신되기 때문에, 날씨 데이터 갱신을 위한 메서드
     *
     * @param loc API를 호출한 사용자의 Location 엔티티
     * @return 해당 위치의 날씨 데이터 List
     */
    @Async("threadPoolTaskExecutor")
    @Transactional
    public void updateWeathers(Location loc) {
        Location location = locationRepository.safeFindById(loc.getId());

        List<Weather> newWeatherList = getPreprocessWeatherList(location);

        List<Weather> orderWeatherList = new ArrayList<>(weatherRepository.findAllWithLocation(location, LocalDateTime.now().plusHours(24)).stream()
                .sorted(Comparator.comparing(Weather::getPresentationTime))
                .toList());

        int minLen = Math.min(newWeatherList.size(), orderWeatherList.size());
        int maxLen = Math.max(newWeatherList.size(), orderWeatherList.size());

        for (int i = 0; i < minLen; i++) {
            Weather orderWeather = orderWeatherList.get(i);
            Weather newWeather = newWeatherList.get(i);
            orderWeather.updateWeather(newWeather);
        }

        List<Weather> subList = new ArrayList<>(newWeatherList.subList(minLen, maxLen));
        subList.forEach(weather -> weather.addLocation(location));
        orderWeatherList.addAll(subList);

        weatherRepository.saveAll(orderWeatherList);
    }

    /**
     * 예보 시간이 현재 보다 과거이면 모두 삭제합니다.
     */
    // TODO: 비동기 처리
    public void bulkDeletePastWeather() {
        weatherRepository.bulkDeletePastWeathers();
    }
}
