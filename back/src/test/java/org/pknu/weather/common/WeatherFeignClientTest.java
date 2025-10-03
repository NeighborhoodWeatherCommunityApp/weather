//package org.pknu.weather.common;
//
//import lombok.extern.slf4j.Slf4j;
//import org.assertj.core.api.Assertions;
//import org.junit.jupiter.api.Test;
//import org.pknu.weather.common.formatter.DateTimeFormatter;
//import org.pknu.weather.weather.dto.WeatherApiResponse;
//import org.pknu.weather.weather.feignClient.dto.PointDTO;
//import org.pknu.weather.feignClient.dto.WeatherParams;
//import org.pknu.weather.feignClient.WeatherFeignClient;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.test.context.SpringBootTest;
//
//@SpringBootTest
//@Slf4j
//class WeatherFeignClientTest {
//
//    @Autowired
//    WeatherFeignClient weatherFeignClient;
//
//    @Value("${api.weather.service-key}")
//    private String weatherServiceKey;
//
//    @Test
//    void open_fegin을_이용한_단기예보_api_동작_테스트() {
//        WeatherParams weatherParams = WeatherFeignClientUitls.create(
//                weatherServiceKey,
//                DateTimeFormatter.getFormattedDate(),
//                DateTimeFormatter.getFormattedTimeByThreeHour(),
//                new PointDTO(55, 127)
//        );
//
//        WeatherApiResponseDTO weatherApiResponses = weatherFeignClient.getVillageShortTermForecast(weatherParams);
//
//        Assertions.assertThat(weatherApiResponses.getResponse().getBody().getItems().getItemList().size()).isEqualTo(288);
//    }
//}
//
//
