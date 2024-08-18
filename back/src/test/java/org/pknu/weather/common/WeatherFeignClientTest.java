package org.pknu.weather.common;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.pknu.weather.dto.WeatherApiResponse;
import org.pknu.weather.dto.WeatherParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
class WeatherFeignClientTest {

    @Autowired
    WeatherFeignClient weatherFeignClient;

    @Test
    void open_fegin을_이용한_단기예보_api_동작_테스트() {
        WeatherParams weatherParams = WeatherParams.builder()
                .base_date(DateTimeFormaterUtils.getFormattedDate())
                .base_time(DateTimeFormaterUtils.getFormattedTimeByThreeHour())
                .nx(55)
                .ny(127)
                .build();

        WeatherApiResponse weatherApiResponses = weatherFeignClient.getVillageShortTermForecast(weatherParams);

        Assertions.assertThat(weatherApiResponses.getResponse().getBody().getItems().getItemList().size()).isEqualTo(288);
    }
}