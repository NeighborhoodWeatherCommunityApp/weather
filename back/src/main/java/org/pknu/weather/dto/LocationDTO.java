package org.pknu.weather.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LocationDTO {
    private Double longitude;

    private Double latitude;

    private String province;

    private String city;

    private String street;

    @JsonIgnore
    public String getFullAddress() {
        return String.join(" ", this.province, this.city, this.street);
    }
}