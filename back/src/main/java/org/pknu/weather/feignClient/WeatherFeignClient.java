package org.pknu.weather.feignClient;

import org.pknu.weather.common.formatter.DateTimeFormatter;
import org.pknu.weather.common.utils.GeometryUtils;
import org.pknu.weather.feignClient.utils.WeatherApiUtils;
import org.pknu.weather.common.WeatherParamsFactory;
import org.pknu.weather.domain.Weather;
import org.pknu.weather.dto.Point;
import org.pknu.weather.dto.WeatherApiResponse;
import org.pknu.weather.dto.WeatherParams;
import org.pknu.weather.dto.WeatherApiResponse.Response.Body.Items.Item;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.Map;

@FeignClient(value = "weather", url = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0")
public interface WeatherFeignClient {

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/getVilageFcst",
            produces = "application/json")
    WeatherApiResponse getVillageShortTermForecast(@SpringQueryMap WeatherParams weatherRequest);

    /**
     * 사용자의 위도 경도 및 기타 정보를 받아와 Point(x, y)로 치환하고 weather로 반환한다.
     *
     * @param lon 경도
     * @param lat 위도
     * @return now ~ 24 시간의 Wether 엔티티를 담고있는 map
     */
    default Map<String, Weather> preprocess(Float lon, Float lat) {
        Point point = GeometryUtils.coordinateToPoint(lon, lat);
        String date = DateTimeFormatter.getFormattedDate();
        String time = DateTimeFormatter.getFormattedTimeByThreeHour();
        WeatherParams weatherParams = WeatherParamsFactory.create(date, time, point);

        WeatherApiResponse weatherApiResponse = getVillageShortTermForecast(weatherParams);
        List<Item> itemList = weatherApiResponse.getResponse().getBody().getItems().getItemList();

        return WeatherApiUtils.responseProcess(itemList, date, time);
    }
}