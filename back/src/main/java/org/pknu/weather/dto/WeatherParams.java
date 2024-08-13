package org.pknu.weather.dto;


import lombok.*;

@Getter
public class WeatherParams {

    @Builder.Default
    private String serviceKey = "%2BTr3T2Oz8rE41Pb37Hj%2BdJIBtR7WSkr73xNNd%2FS9YCyBagavmwIlWFjV0ZgBWwTpHL0mp01fgJiHAn7PzbTU0Q%3D%3D";

    @Builder.Default
    private Integer numOfRows = 288;

    @Builder.Default
    private Integer pageNo = 1;

    @Builder.Default
    private String dataType = "JSON";

    private String base_date;

    private String base_time;

    private Integer nx;

    private Integer ny;

    @Builder
    private WeatherParams(String base_date, String base_time, Integer nx, Integer ny) {
        this.base_date = base_date;
        this.base_time = base_time;
        this.nx = nx;
        this.ny = ny;
    }
}
