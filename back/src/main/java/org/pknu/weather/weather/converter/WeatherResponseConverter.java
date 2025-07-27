package org.pknu.weather.weather.converter;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import org.pknu.weather.common.utils.TagUtils;
import org.pknu.weather.domain.Location;
import org.pknu.weather.domain.Member;
import org.pknu.weather.weather.Weather;
import org.pknu.weather.domain.tag.RainTag;
import org.pknu.weather.domain.tag.TemperatureTag;
import org.pknu.weather.dto.TagDto;
import org.pknu.weather.weather.dto.WeatherQueryResult;
import org.pknu.weather.weather.dto.WeatherResponse;

public class WeatherResponseConverter {

    public static WeatherResponse.MainPageWeatherData toMainPageWeatherData(List<Weather> weatherList, Member member) {
        int max = Integer.MIN_VALUE;
        int min = Integer.MAX_VALUE;

        for (Weather w : weatherList) {
            max = Math.max(max, w.getTemperature());
            min = Math.min(min, w.getTemperature());
        }

        List<WeatherResponse.WeatherPerHour> weatherPerHourList = weatherList.stream()
                .map(weather -> toWeatherPerHour(weather, member))
                .toList();

        Location location = member.getLocation();
        Weather now = weatherList.get(0);
        now.updateSensibleTemperature();

        return WeatherResponse.MainPageWeatherData.builder()
                .city(location.getCity())
                .street(location.getStreet())
                .currentSkyType(now.getSkyType())
                .currentTmp(now.getTemperature())
                .currentSensibleTmp(now.getSensibleTemperature())
                .weatherPerHourList(weatherPerHourList)
                .temperature(toTemperature(max, min))
                .build();
    }

    public static WeatherResponse.WeatherPerHour toWeatherPerHour(Weather weather, Member member) {
        RainTag rainTag = (RainTag) TagUtils.rainType2RainTag(weather);
        TemperatureTag temperatureTag = TagUtils.tmp2TemperatureTag(weather.getTemperature(), member.getSensitivity());

        return WeatherResponse.WeatherPerHour.builder()
                .hour(weather.getPresentationTime())
                .skyType(weather.getSkyType())
                .rainAdverb(rainTag.getAdverb())
                .rainText(rainTag.getText())
                .rain(weather.getRain())
                .tmpAdverb(temperatureTag.getAdverb())
                .tmpText(temperatureTag.getText())
                .tmp(weather.getTemperature())
                .build();
    }

    public static WeatherResponse.Temperature toTemperature(Integer max, Integer min) {
        return WeatherResponse.Temperature.builder()
                .maxTmp(max)
                .minTmp(min)
                .build();
    }

    public static WeatherResponse.WeatherSimpleInfo toSimpleWeatherInfo(List<TagDto.SimpleTag> tagList) {
        return WeatherResponse.WeatherSimpleInfo.builder()
                .tags(tagList.stream().map(TagDto.SimpleTag::getText).toList())
                .prcpProb(null)
                .days(null)
                .build();
    }

    public static WeatherResponse.SimpleRainInformation toSimpleRainInformation(
            WeatherQueryResult.SimpleRainInfo simpleRainInfo) {
        if (simpleRainInfo == null) {
            return WeatherResponse.SimpleRainInformation.builder()
                    .rainComment("오늘은 비소식이 없어요")
                    .addComment("")
                    .willRain(false)
                    .rainfallAmount("0.0mm")
                    .build();
        }

        long hours = Duration.between(LocalDateTime.now(), simpleRainInfo.getTime()).toHours();
        StringBuilder sb = new StringBuilder();

        if (hours == 0) {
            sb.append("잠시 후에 ");
        } else {
            sb.append(hours + "시간 뒤에 ");
        }

        if (simpleRainInfo.getSnowCover() > 0) {
            sb.append("눈 ");
        } else {
            sb.append("비 ");
        }
        sb.append("소식이 있어요.");

        return WeatherResponse.SimpleRainInformation.builder()
                .rainComment(sb.toString())
                .addComment("외출할 때 우산 꼭 챙기세요!")
                .willRain(true)
                .rainfallAmount(simpleRainInfo.getRain() + "mm")
                .build();
    }
}
