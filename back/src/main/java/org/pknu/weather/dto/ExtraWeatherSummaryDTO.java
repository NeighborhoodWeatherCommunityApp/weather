package org.pknu.weather.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExtraWeatherSummaryDTO {

    private Long locationId;

    private Integer pm10;

    private String maxUvTime;

    private Integer maxUvValue;

}
