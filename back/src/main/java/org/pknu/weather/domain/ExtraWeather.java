package org.pknu.weather.domain;

import jakarta.persistence.*;
import lombok.*;
import org.pknu.weather.dto.MemberJoinDTO;
import org.pknu.weather.dto.WeatherResponse;

import java.time.LocalDateTime;

/**
 *  pm10: 미세먼지(10)
 *  pm25: 초미세먼지(2.5)
 */
@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ExtraWeather extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "extra_weather_id")
    private Long id;

    private LocalDateTime basetime;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "location_id")
    private Location location;

    private Integer uv;
    private Integer uvPlus3;
    private Integer uvPlus6;
    private Integer uvPlus9;
    private Integer uvPlus12;
    private Integer uvPlus15;
    private Integer uvPlus18;
    private Integer uvPlus21;
    private Integer o3;
    private Integer pm10;
    private Integer pm25;
    private Integer pm10value;
    private Integer pm25value;

    public void updateExtraWeather (WeatherResponse.ExtraWeatherInfo extraWeatherInfo){
        this.basetime = extraWeatherInfo.getBaseTime();
        if (extraWeatherInfo.getUvGrade() != null)
            this.uv = extraWeatherInfo.getUvGrade();

        if (extraWeatherInfo.getUvGrade() != null)
            this.uvPlus3 = extraWeatherInfo.getUvGradePlus3();

        if (extraWeatherInfo.getUvGrade() != null)
            this.uvPlus6 = extraWeatherInfo.getUvGradePlus6();

        if (extraWeatherInfo.getUvGrade() != null)
            this.uvPlus9 = extraWeatherInfo.getUvGradePlus9();

        if (extraWeatherInfo.getUvGrade() != null)
            this.uvPlus12 = extraWeatherInfo.getUvGradePlus12();

        if (extraWeatherInfo.getUvGrade() != null)
            this.uvPlus15 = extraWeatherInfo.getUvGradePlus15();

        if (extraWeatherInfo.getUvGrade() != null)
            this.uvPlus18 = extraWeatherInfo.getUvGradePlus18();

        if (extraWeatherInfo.getUvGrade() != null)
            this.uvPlus21 = extraWeatherInfo.getUvGradePlus21();

        if (extraWeatherInfo.getO3Grade() != null)
            this.o3 = extraWeatherInfo.getO3Grade();

        if (extraWeatherInfo.getPm10Grade() != null)
            this.pm10 = extraWeatherInfo.getPm10Grade();

        if (extraWeatherInfo.getPm25Grade() != null)
            this.pm25 = extraWeatherInfo.getPm25Grade();

        if (extraWeatherInfo.getPm10Value() != null)
            this.pm10value = extraWeatherInfo.getPm10Value();

        if (extraWeatherInfo.getPm25Value() != null)
            this.pm25value = extraWeatherInfo.getPm25Value();

    }
}
