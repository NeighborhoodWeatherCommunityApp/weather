package org.pknu.weather.feignClient.dto;


import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
public class WeatherParams {
    private String serviceKey;

    @Builder.Default
    private String dataType = "JSON";

    private Integer pageNo;

    private Integer numOfRows;

    private String base_date;

    private String base_time;

    private Integer nx;

    private final Integer ny;

    @Builder
    private WeatherParams(String serviceKey, Integer pageNo, Integer numOfRows, String base_date, String base_time, Integer nx, Integer ny) {
        this.serviceKey = serviceKey;
        this.pageNo = pageNo;
        this.numOfRows = numOfRows;
        this.base_date = base_date;
        this.base_time = base_time;
        this.nx = nx;
        this.ny = ny;
    }
}
