package org.pknu.weather.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WeatherSummaryDTO {

    private Long locationId;

    private String rainStatus;

    private Integer maxTemp;

    private Integer minTemp;

}
