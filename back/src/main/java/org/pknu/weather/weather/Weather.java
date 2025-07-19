package org.pknu.weather.weather;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.pknu.weather.common.utils.SensibleTemperatureUtils;
import org.pknu.weather.domain.BaseEntity;
import org.pknu.weather.domain.Location;
import org.pknu.weather.domain.common.RainType;
import org.pknu.weather.domain.common.SkyType;
import org.pknu.weather.weather.dto.WeatherApiResponse;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "weather", uniqueConstraints = {
        @UniqueConstraint(
                name = "location_id_presentation_time_unique",
                columnNames = {"location_id", "presentation_time"}
        )})
public class Weather extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "weather_id")
    private Long id;

    @Column(name = "presentation_time", nullable = false)
    private LocalDateTime presentationTime;

    private LocalDateTime basetime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private Location location;

    private Double windSpeed;

    private Integer humidity;

    private Integer rainProb;

    private Float rain;

    private RainType rainType;

    private Integer temperature;

    private Double sensibleTemperature;

    private Float snowCover;

    private SkyType skyType;


    @PrePersist
    @PreUpdate
    public void updateSensibleTemperature() {
        double sensibleTemperature = SensibleTemperatureUtils.getSensibleTemperature(getTemperature(), getHumidity(),
                getWindSpeed());

        setSensibleTemperature(sensibleTemperature);
    }

    // location 과의 연관 관계 편의 메소드
    public void addLocation(Location location) {
        location.getWeatherList().add(this);
        this.location = location;
    }

    public void updateWeather(Weather newWeather) {
        if (!this.rainProb.equals(newWeather.getRainProb())) {
            setRainProb(newWeather.getRainProb());
        }
        if (!this.rainType.equals(newWeather.getRainType())) {
            setRainType(newWeather.getRainType());
        }
        if (!this.rain.equals(newWeather.getRain())) {
            setRain(newWeather.getRain());
        }
        if (!this.humidity.equals(newWeather.getHumidity())) {
            setHumidity(newWeather.getHumidity());
        }
        if (!this.snowCover.equals(newWeather.getSnowCover())) {
            setSnowCover(newWeather.getSnowCover());
        }
        if (!this.skyType.equals(newWeather.getSkyType())) {
            setSkyType(newWeather.getSkyType());
        }
        if (!this.temperature.equals(newWeather.getTemperature())) {
            setTemperature(newWeather.getTemperature());
        }
        if (!this.windSpeed.equals(newWeather.getWindSpeed())) {
            setWindSpeed(newWeather.getWindSpeed());
        }
        if (!this.basetime.equals(newWeather.getBasetime())) {
            setBasetime(newWeather.getBasetime());
        }
        if (!this.presentationTime.equals(newWeather.getPresentationTime())) {
            setPresentationTime(newWeather.getPresentationTime());
        }
    }

    public void categoryClassify(WeatherApiResponse.Response.Body.Items.Item item) {
        String val = item.getFcstValue();
        switch (item.getCategory()) {
            // 강수 확률
            case "POP" -> setRainProb(Integer.parseInt(val));
            // 강수 형태
            case "PTY" -> setRainType(convertRainType(val));
            // 1시간 강수량
            case "PCP" -> setRain(val.equals("강수없음") ? 0.0f : Float.parseFloat(val.split("[~mm]+")[0]));
            // 습도
            case "REH" -> setHumidity(Integer.parseInt(val));
            // 1시간 신척설
            case "SNO" -> setSnowCover(val.equals("적설없음") ? 0.0f : Float.parseFloat(val.split("[~cm]+")[0]));
            // 하늘 상태
            case "SKY" -> setSkyType(convertSkyType(val));
            // 1시간 기온
            case "TMP" -> setTemperature(Integer.parseInt(val));
            // 풍속
            case "WSD" -> setWindSpeed(Double.parseDouble(val));
        }
    }

    private RainType convertRainType(String val) {
        switch (val) {
            case "1":
                return RainType.RAIN;
            case "2":
                return RainType.RAIN_AND_SNOW;
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

    private void setWindSpeed(Double windSpeed) {
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

    private void setSensibleTemperature(Double sensibleTemperature) {
        this.sensibleTemperature = sensibleTemperature;
    }

    private void setSnowCover(Float snowCover) {
        this.snowCover = snowCover;
    }

    private void setSkyType(SkyType skyType) {
        this.skyType = skyType;
    }

    private void setBasetime(LocalDateTime basetime) {
        this.basetime = basetime;
    }

    private void setPresentationTime(LocalDateTime presentationTime) {
        this.presentationTime = presentationTime;
    }
}
