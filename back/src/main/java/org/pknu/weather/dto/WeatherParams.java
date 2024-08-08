package org.pknu.weather.dto;


import lombok.*;

@Builder
@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public class WeatherParams {

//    @Value("${api.weather.service_key}")
    @Builder.Default
    private String serviceKey = "%2BTr3T2Oz8rE41Pb37Hj%2BdJIBtR7WSkr73xNNd%2FS9YCyBagavmwIlWFjV0ZgBWwTpHL0mp01fgJiHAn7PzbTU0Q%3D%3D";

    private Integer numOfRows;

    private Integer pageNo;

//    @Value("${api.weather.dataType")
    @Builder.Default
    private String dataType = "JSON";

    private String base_date;

    private String base_time;

    private Integer nx;

    private Integer ny;
}
