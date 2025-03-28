package org.pknu.weather.domain;

import jakarta.persistence.*;
import lombok.*;

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

    @Builder.Default
    @OneToMany(mappedBy = "location",
            fetch = FetchType.LAZY)
    private List<Tag> TagList = new ArrayList<>();

    public String getAddress() {
        return getCity() + getStreet();
    }

    public String getFullAddress() {
        return getProvince() + " " + getCity() + " " + getStreet();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        return Objects.equals(id, location.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
