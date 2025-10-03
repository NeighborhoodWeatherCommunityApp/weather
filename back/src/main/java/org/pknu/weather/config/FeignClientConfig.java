package org.pknu.weather.config;

import feign.Request;
import feign.codec.ErrorDecoder;
import java.util.HashMap;
import java.util.Map;
import org.pknu.weather.weather.feignclient.error.CommonErrorDecoder;
import org.pknu.weather.weather.feignclient.error.WeatherFeignErrorDecoder;
import org.pknu.weather.weather.feignclient.weatherapi.OpenApiFeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignClientConfig {

    // 연결 타임아웃 & 읽기 타임아웃 설정 (5초)
    @Bean
    public Request.Options feignOptions() {
        return new Request.Options(5000, 5000);
    }

    @Bean
    public ErrorDecoder errorDecoder() {
        return new CommonErrorDecoder(errorDecoderMap());
    }

    @Bean
    public Map<String, ErrorDecoder> errorDecoderMap() {
        Map<String, ErrorDecoder> map = new HashMap<>();

        map.put(OpenApiFeignClient.class.getSimpleName(), new WeatherFeignErrorDecoder());

        return map;
    }
}
