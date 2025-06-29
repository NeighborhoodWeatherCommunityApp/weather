package org.pknu.weather.service.message;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class WeatherAlarmMessageBuilder {
    private String rainStatus;
    private Integer maxTemp;
    private Integer minTemp;
    private Integer pm10;
    private String maxUvTime;
    private Integer maxUvValue;
    private static final String STRING_FORMAT = "%-24s";



    public WeatherAlarmMessageBuilder withRainStatus(String rainStatus) {
        this.rainStatus = rainStatus;
        return this;
    }

    public WeatherAlarmMessageBuilder withTemperature(Integer maxTemp, Integer minTemp) {
        this.maxTemp = maxTemp;
        this.minTemp = minTemp;
        return this;
    }

    public WeatherAlarmMessageBuilder withDust(Integer pm10) {
        this.pm10 = pm10;
        return this;
    }

    public WeatherAlarmMessageBuilder withUV(String maxUvTime, Integer maxUvValue) {
        this.maxUvTime = maxUvTime;
        this.maxUvValue = maxUvValue;
        return this;
    }

    public String build() {
        List<String> messageParts = new ArrayList<>();

        appendTemperature(messageParts);
        appendRainStatus(messageParts);
        appendDustLevel(messageParts);
        appendUvLevel(messageParts);

        return formatMessage(messageParts);
    }

    private void appendTemperature(List<String> messageParts) {
        if (maxTemp != null && minTemp != null)
            messageParts.add("🌡️ " + String.format(STRING_FORMAT, "기온: " + minTemp + "°C / " + maxTemp + "°C"));
    }

    private void appendRainStatus(List<String> messageParts) {
        if (rainStatus != null)
            messageParts.add("☔️ " + String.format(STRING_FORMAT, "강수 상태: " + rainStatus));
    }

    private void appendDustLevel(List<String> messageParts) {
        if (pm10 != null)
            messageParts.add("🌫️ " + String.format(STRING_FORMAT, "미세먼지: " + getDustLevel(pm10)));
    }

    private void appendUvLevel(List<String> messageParts) {
        if (maxUvTime != null && maxUvValue != null)
            messageParts.add("🌞 " + String.format(STRING_FORMAT, "자외선: " + getUvLevel(maxUvValue) + " - " + maxUvTime + "시"));
    }

    private String formatMessage(List<String> messageParts) {

        StringJoiner stringJoiner = new StringJoiner("\n");

        for (String part : messageParts) {
            stringJoiner.add(part);
        }

        return stringJoiner.toString().trim();
    }

    private String getDustLevel(int pm10) {
        if (pm10 == 1) return "좋음";
        if (pm10 == 2) return "보통";
        if (pm10 == 3) return "나쁨";
        return "매우 나쁨";
    }

    private String getUvLevel(int uv) {
        if (uv == 1) return "좋음";
        if (uv == 2) return "보통";
        if (uv == 3) return "나쁨";
        return "매우 나쁨";
    }
}