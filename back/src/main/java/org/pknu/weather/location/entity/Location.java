package org.pknu.weather.location.entity;

import jakarta.persistence.*;
import lombok.*;
import org.pknu.weather.common.entity.BaseEntity;
import org.pknu.weather.tag.entity.Tag;
import org.pknu.weather.weather.Weather;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Location extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @Builder.Default
    @OneToMany(mappedBy = "location")
    private List<Tag> TagList = new ArrayList<>();

    public String getAddress() {
        return getCity() + getStreet();
    }

    public String getFullAddress() {
        return getProvince() + " " + getCity() + " " + getStreet();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Location location = (Location) o;
        return Objects.equals(id, location.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
