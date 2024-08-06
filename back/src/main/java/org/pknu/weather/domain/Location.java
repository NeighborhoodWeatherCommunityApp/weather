package org.pknu.weather.domain;
import jakarta.persistence.*;
import lombok.*;
import org.pknu.weather.common.BaseEntity;

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
}
