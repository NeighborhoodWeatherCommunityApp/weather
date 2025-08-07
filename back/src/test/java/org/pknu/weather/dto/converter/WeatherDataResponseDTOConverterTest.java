package org.pknu.weather.dto.converter;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.pknu.weather.weather.dto.WeatherQueryResultDTO.SimpleRainInfo;
import org.pknu.weather.weather.dto.WeatherResponseDTO.SimpleRainInformation;
import org.pknu.weather.weather.converter.WeatherResponseConverter;

import java.time.LocalDateTime;
import java.util.stream.Stream;

class WeatherDataResponseDTOConverterTest {

    @ParameterizedTest
    @MethodSource("source")
    void 눈_비_간단_예보_변환_테스트(LocalDateTime time, Integer prob, Float rain, Float snow, String expect) {
        // given
        SimpleRainInfo simpleRainInfo = SimpleRainInfo.builder()
                .time(time)
                .rainProbability(prob)
                .rain(rain)
                .snowCover(snow)
                .build();

        // when
        SimpleRainInformation simpleRainInformation = WeatherResponseConverter.toSimpleRainInformation(simpleRainInfo);

        // then
        Assertions.assertThat(simpleRainInformation.getRainComment()).contains(expect);
    }

    private static Stream<Arguments> source() {
        return Stream.of(
                Arguments.of(LocalDateTime.now().plusMinutes(30), 30, 1.0f, 0.0f, "비 소식이 있어요."),        // 비 올 때
                Arguments.of(LocalDateTime.now().plusMinutes(30), 30, 0.0f, 1.0f, "눈 소식이 있어요.")        // 눈 올 때
        );
    }
}