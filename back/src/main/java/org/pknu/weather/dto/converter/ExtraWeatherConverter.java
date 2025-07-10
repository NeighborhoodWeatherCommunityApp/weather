package org.pknu.weather.dto.converter;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import org.pknu.weather.domain.ExtraWeather;
import org.pknu.weather.domain.Location;
import org.pknu.weather.dto.ExtraWeatherSummaryDTO;
import org.pknu.weather.dto.WeatherResponse;

public class ExtraWeatherConverter {

    public static WeatherResponse.ExtraWeatherInfo toExtraWeatherInfo(ExtraWeather extraWeather) {
        return WeatherResponse.ExtraWeatherInfo.builder()
                .baseTime(extraWeather.getBasetime())
                .uvGrade(extraWeather.getUv())
                .uvGradePlus3(extraWeather.getUvPlus3())
                .uvGradePlus6(extraWeather.getUvPlus6())
                .uvGradePlus9(extraWeather.getUvPlus9())
                .uvGradePlus12(extraWeather.getUvPlus12())
                .uvGradePlus15(extraWeather.getUvPlus15())
                .uvGradePlus18(extraWeather.getUvPlus18())
                .uvGradePlus21(extraWeather.getUvPlus21())
                .o3Grade(extraWeather.getO3())
                .pm10Grade(extraWeather.getPm10())
                .pm10Value(extraWeather.getPm10value())
                .pm25Grade(extraWeather.getPm25())
                .pm25Value(extraWeather.getPm25value())
                .build();
    }

    public static ExtraWeather toExtraWeather(Location location, WeatherResponse.ExtraWeatherInfo extraWeatherInfo) {
        return ExtraWeather.builder()
                .location(location)
                .basetime(extraWeatherInfo.getBaseTime())
                .uv(extraWeatherInfo.getUvGrade())
                .uvPlus3(extraWeatherInfo.getUvGradePlus3())
                .uvPlus6(extraWeatherInfo.getUvGradePlus6())
                .uvPlus9(extraWeatherInfo.getUvGradePlus9())
                .uvPlus12(extraWeatherInfo.getUvGradePlus12())
                .uvPlus15(extraWeatherInfo.getUvGradePlus15())
                .uvPlus18(extraWeatherInfo.getUvGradePlus18())
                .uvPlus21(extraWeatherInfo.getUvGradePlus21())
                .o3(extraWeatherInfo.getO3Grade())
                .pm10(extraWeatherInfo.getPm10Grade())
                .pm10value(extraWeatherInfo.getPm10Value())
                .pm25(extraWeatherInfo.getPm25Grade())
                .pm25value(extraWeatherInfo.getPm25Value())
                .build();
    }

    public static ExtraWeatherSummaryDTO toExtraWeatherSummaryDTO(ExtraWeather extraWeather) {
        List<UvData> uvDataList = getUvData(extraWeather);

        LocalDateTime tomorrow = LocalDateTime.now().plusDays(1).with(LocalTime.MIDNIGHT);

        UvData maxUv = uvDataList.stream()
                .filter(data -> data.value() != null)
                .filter(data -> data.time().isBefore(tomorrow))
                .max(Comparator.comparing(UvData::value))
                .orElse(new UvData(0, null));

        return ExtraWeatherSummaryDTO.builder()
                .locationId(extraWeather.getLocation().getId())
                .pm10(extraWeather.getPm10())
                .maxUvValue(maxUv.value())
                .maxUvTime(String.valueOf(maxUv.time().getHour()))
                .build();
    }

    private static List<UvData> getUvData(ExtraWeather extraWeather) {
        LocalDateTime baseTime = extraWeather.getBasetime();

        return List.of(
                new UvData(extraWeather.getUv(), baseTime.plusHours(0)),
                new UvData(extraWeather.getUvPlus3(), baseTime.plusHours(3)),
                new UvData(extraWeather.getUvPlus6(), baseTime.plusHours(6)),
                new UvData(extraWeather.getUvPlus9(), baseTime.plusHours(9)),
                new UvData(extraWeather.getUvPlus12(), baseTime.plusHours(12)),
                new UvData(extraWeather.getUvPlus15(), baseTime.plusHours(15)),
                new UvData(extraWeather.getUvPlus18(), baseTime.plusHours(18)),
                new UvData(extraWeather.getUvPlus21(), baseTime.plusHours(21))
        );
    }
    public record UvData(Integer value, LocalDateTime time) {}

}
