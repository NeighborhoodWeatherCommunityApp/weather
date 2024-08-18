package org.pknu.weather.domain;

import jakarta.persistence.*;
import lombok.*;
import org.pknu.weather.common.BaseEntity;
import org.pknu.weather.domain.common.RainType;
import org.pknu.weather.domain.common.SkyType;
import org.pknu.weather.dto.WeatherApiResponse;

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

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
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

    // location 과의 연관 관계 편의 메소드
    public void addLocation(Location location) {
        location.getWeatherList().add(this);
        this.location = location;
    }

    public void categoryClassify(WeatherApiResponse.Response.Body.Items.Item item) {
        String val = item.getFcstValue();
        switch (item.getCategory()) {
            // 강수 확률
            case "POP" -> this.setRainProb(Integer.parseInt(val));
            // 강수 형태
            case "PTY" -> this.setRainType(convertRainType(val));
            // 1시간 강수량
            case "PCP" -> this.setRain(val.equals("강수없음") ? 0.0f : Float.parseFloat(val.split("mm")[0]));
            // 습도
            case "REH" -> this.setHumidity(Integer.parseInt(val));
            // 1시간 신척설
            case "SNO" -> this.setSnowCover(val.equals("적설없음") ? 0.0f : Float.parseFloat(val.split("cm")[0]));
            // 하늘 상태
            case "SKY" -> this.setSkyType(convertSkyType(val));
            // 1시간 기온
            case "TMP" -> this.setTemperature(Integer.parseInt(val));
            // 일 최저 기온
//            case "TMN" -> this.setMinTemperature(Integer.parseInt(val));
            // 일 최고 기온
//            case "TMX" -> this.setMaxTemperature(Integer.parseInt(val));
            // 풍속
            case "WSD" -> this.setWindSpeed(Float.parseFloat(val));
        }
    }

    private RainType convertRainType(String val) {
        switch (val) {
            case "1":
                return RainType.RAIN;
            case "2":
                return RainType.RAINANDSNOW;
            case "3":
                return RainType.SNOW;
            case "4":
                return RainType.SHOWER;
            default:
                return RainType.NONE;
        }
    }

    private SkyType convertSkyType(String val) {
        switch (val) {
            case "3":
                return SkyType.CLOUDY;
            case "4":
                return SkyType.PARTLYCLOUDY;
            default:
                return SkyType.CLEAR;
        }
    }

    private void setWindSpeed(Float windSpeed) {
        this.windSpeed = windSpeed;
    }

    private void setHumidity(Integer humidity) {
        this.humidity = humidity;
    }

    private void setRainProb(Integer rainProb) {
        this.rainProb = rainProb;
    }

    private void setRain(Float rain) {
        this.rain = rain;
    }

    private void setRainType(RainType rainType) {
        this.rainType = rainType;
    }

    private void setTemperature(Integer temperature) {
        this.temperature = temperature;
    }

    private void setMaxTemperature(Integer maxTemperature) {
        this.maxTemperature = maxTemperature;
    }

    private void setMinTemperature(Integer minTemperature) {
        this.minTemperature = minTemperature;
    }

    private void setSnowCover(Float snowCover) {
        this.snowCover = snowCover;
    }

    private void setSkyType(SkyType skyType) {
        this.skyType = skyType;
    }
}
