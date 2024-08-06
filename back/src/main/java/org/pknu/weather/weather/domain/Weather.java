package org.pknu.weather.weather.domain;
import jakarta.persistence.*;
import lombok.*;
import org.pknu.weather.common.BaseEntity;
import org.pknu.weather.location.domain.Location;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Weather extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "weather_id")
    private Long id;

    private LocalDateTime basetime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private Location location;

    private Float windSpeed;

    private Integer humidity;

    private Integer rainProb;

    private Float rain;

    private RainType rainType;

    private Integer temperature;

    private Integer maxTemperature;

    private Integer minTemperature;

    private Float snowCover;

    private SkyType skyType;

    private LocalDateTime presentationTime;


}
