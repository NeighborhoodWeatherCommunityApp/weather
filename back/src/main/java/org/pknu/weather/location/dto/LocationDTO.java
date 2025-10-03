package org.pknu.weather.location.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.locationtech.jts.geom.Point;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LocationDTO {
    private Long id;

    private Double longitude;

    private Double latitude;

    private String province;

    private String city;

    private String street;

    @JsonIgnore
    private Point point;

    @JsonIgnore
    public String getFullAddress() {
        return String.join(" ", this.province, this.city, this.street);
    }
}
