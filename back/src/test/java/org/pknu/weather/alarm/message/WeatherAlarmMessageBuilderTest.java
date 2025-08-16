package org.pknu.weather.alarm.message;


import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.pknu.weather.alarm.message.WeatherAlarmMessageBuilder;

class WeatherAlarmMessageBuilderTest {

    private static final String STRING_FORMAT = "%-24s";

    @Test
    void 모든_항목_동의_메시지_작성_테스트() {
        // Given
        WeatherAlarmMessageBuilder builder = new WeatherAlarmMessageBuilder();

        builder.withTemperature(25, 15)
                .withRainStatus("맑음")
                .withDust(2)
                .withUV("12", 3);

        // When
        String message = builder.build();

        // Then
        String expectedMessage = "🌡️ " + String.format(STRING_FORMAT, "기온: " + 15 + "°C / " + 25 + "°C") + "\n" +
                "☔️ " + String.format(STRING_FORMAT, "강수 상태: 맑음") + "\n" +
                "🌫️ " + String.format(STRING_FORMAT, "미세먼지: 보통") + "\n" +
                "🌞 " + String.format(STRING_FORMAT, "자외선: 나쁨" + " - " + 12 + "시").trim();

        assertThat(message).isEqualTo(expectedMessage);
    }

    @Test
    void 기온과_미세먼지_항목_동의_메시지_작성_테스트() {
        // Given
        WeatherAlarmMessageBuilder builder = new WeatherAlarmMessageBuilder();
        builder.withTemperature(20, 10)
                .withDust(2);

        // When
        String message = builder.build();

        // Then
        String expectedMessage = "🌡️ " + String.format(STRING_FORMAT, "기온: " + 10 + "°C / " + 20 + "°C") + "\n" +
                "🌫️ " + String.format(STRING_FORMAT, "미세먼지: 보통").trim();

        assertThat(message).isEqualTo(expectedMessage);
    }

    @Test
    void 강수와_자외선_항목_동의_메시지_작성_테스트() {
        // Given
        WeatherAlarmMessageBuilder builder = new WeatherAlarmMessageBuilder();
        builder.withRainStatus("비")
                .withUV("15", 3);

        // When
        String message = builder.build();

        // Then
        String expectedMessage = "☔️ " + String.format(STRING_FORMAT, "강수 상태: 비") + "\n" +
                "🌞 " + String.format(STRING_FORMAT, "자외선: 나쁨" + " - " + 15 + "시").trim();

        assertThat(message).isEqualTo(expectedMessage);
    }

    @Test
    void 빈_항목_동의_메시지_작성_테스트() {
        // Given
        WeatherAlarmMessageBuilder builder = new WeatherAlarmMessageBuilder();

        // When
        String message = builder.build();

        // Then
        assertThat(message).isEqualTo("");
    }

    @Test
    void 강수_항목_동의_메시지_작성_테스트() {
        // Given
        WeatherAlarmMessageBuilder builder = new WeatherAlarmMessageBuilder();
        builder.withRainStatus("눈");

        // When
        String message = builder.build();

        // Then
        String expectedMessage = "☔️ " + String.format(STRING_FORMAT, "강수 상태: 눈").trim();

        assertThat(message).isEqualTo(expectedMessage);
    }


    @Test
    void 세_항목_동의_메시지_작성_테스트() {
        // Given
        WeatherAlarmMessageBuilder builder = new WeatherAlarmMessageBuilder();
        builder.withRainStatus("흐림")
                .withTemperature(10, 5)
                .withDust(1);

        // When
        String message = builder.build();

        // Then
        String expectedMessage = "🌡️ " + String.format(STRING_FORMAT, "기온: " + 5 + "°C / " + 10 + "°C") + "\n" +
                "☔️ " + String.format(STRING_FORMAT, "강수 상태: 흐림") + "\n" +
                "🌫️ " + String.format(STRING_FORMAT, "미세먼지: 좋음").trim();

        assertThat(message).isEqualTo(expectedMessage);
    }

}