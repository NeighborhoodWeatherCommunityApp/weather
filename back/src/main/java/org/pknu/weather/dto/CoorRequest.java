package org.pknu.weather.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoorRequest {

    private Double longitude;

    private Double latitude;
}
