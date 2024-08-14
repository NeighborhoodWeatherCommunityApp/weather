package org.pknu.weather.domain;

import jakarta.persistence.*;
import lombok.*;
import org.pknu.weather.common.BaseEntity;
import org.pknu.weather.dto.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Location extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "location_id")
    private Long id;

    private Double longitude;

    private Double latitude;

    private String province;

    private String city;

    private String street;

    @Builder.Default
    @OneToMany(mappedBy = "location",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private List<Weather> weatherList = new ArrayList<>();

    public String getAddress() {
        return getCity() + getStreet();
    }
}
