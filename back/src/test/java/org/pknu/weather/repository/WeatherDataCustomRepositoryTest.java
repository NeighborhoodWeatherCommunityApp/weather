package org.pknu.weather.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.pknu.weather.config.DataJpaTestConfig;
import org.pknu.weather.domain.Location;
import org.pknu.weather.weather.Weather;
import org.pknu.weather.domain.common.RainType;
import org.pknu.weather.weather.dto.WeatherSummaryDTO;
import org.pknu.weather.weather.repository.WeatherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@Import(DataJpaTestConfig.class)
@DataJpaTest
class WeatherDataCustomRepositoryTest {

    @Autowired
    private WeatherRepository weatherRepository;

    @Autowired
    private TestEntityManager entityManager;

    private static MockedStatic<LocalDateTime> mockedLocalDateTimeStatic;

    private static final LocalDateTime FIXED_CURRENT_DATETIME = LocalDateTime.of(2025, 5, 16, 13, 59, 18);


    @BeforeEach
    void beforeAll() {
        mockedLocalDateTimeStatic = Mockito.mockStatic(LocalDateTime.class, Mockito.CALLS_REAL_METHODS);
        mockedLocalDateTimeStatic.when(LocalDateTime::now).thenReturn(FIXED_CURRENT_DATETIME);
    }

    @AfterEach
    void afterEach() {
        if (mockedLocalDateTimeStatic != null) {
            mockedLocalDateTimeStatic.close();
        }
    }

    @Test
    void 날씨_데이터가_없을_때() {

    }

    @Test
    void 위치_목록이_비어_있을_때_빈_리스트_반환() {
        // Given
        Set<Long> locationIds = new HashSet<>();

        // When
        List<WeatherSummaryDTO> result = weatherRepository.findWeatherSummary(locationIds);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void 시간_범위_밖의_날씨_데이터는_결과에_영향을_주지_않도록_설정() {

        // Given
        LocalDateTime now = LocalDateTime.now();
        Location location = createAndPersistLocation();

        LocalDateTime yesterday = now.minusDays(1);
        LocalDateTime tomorrow = now.plusDays(1);
        LocalDateTime endOfYesterday = yesterday.with(LocalTime.MAX);
        LocalDateTime startOfTomorrow = tomorrow.with(LocalTime.MIDNIGHT).plusSeconds(1);

        createAndPersistWeather(location, yesterday, 17, RainType.RAIN);
        createAndPersistWeather(location, endOfYesterday, 10, RainType.RAIN);
        createAndPersistWeather(location, startOfTomorrow, 11, RainType.NONE);
        createAndPersistWeather(location, tomorrow, 15, RainType.SNOW);

        // When
        List<WeatherSummaryDTO> result = weatherRepository.findWeatherSummary(Set.of(location.getId()));

        // Then
        assertThat(result).isEmpty();
    }


    @Test
    void RAIN과_SHOWER가_같이_있는_경우_RAIN으로_설정() {

        // Given
        LocalDateTime now = LocalDateTime.now();
        Location location = createAndPersistLocation();

        createAndPersistWeather(location, now.plusHours(1), 10, RainType.RAIN);
        createAndPersistWeather(location, now.plusHours(2), 12, RainType.SHOWER);
        createAndPersistWeather(location, now.plusHours(3), 11, RainType.NONE);

        // When
        List<WeatherSummaryDTO> result = weatherRepository.findWeatherSummary(Set.of(location.getId()));

        // Then
        assertThat(result)
                .hasSize(1)
                .first()
                .satisfies(summary -> {
                        assertThat(summary.getRainStatus()).isEqualTo("RAIN");
                        assertThat(summary.getMinTemp()).isEqualTo(10);
                        assertThat(summary.getMaxTemp()).isEqualTo(12);
                });
    }

    @Test
    void RAIN_AND_SNOW_타입이_다른_타입과_같이_있는_경우_RAIN_AND_SNOW로_설정() {

        // Given
        LocalDateTime now = LocalDateTime.now();
        Location loc = createAndPersistLocation();

        createAndPersistWeather(loc, now.plusHours(1), 0, RainType.RAIN_AND_SNOW);
        createAndPersistWeather(loc, now.plusHours(2), 2, RainType.RAIN);
        createAndPersistWeather(loc, now.plusHours(3), -1, RainType.SNOW);
        createAndPersistWeather(loc, now.plusHours(4), -1, RainType.SHOWER);

        // When
        List<WeatherSummaryDTO> result = weatherRepository.findWeatherSummary(Set.of(loc.getId()));

        // Then
        assertThat(result)
                .hasSize(1)
                .first()
                .satisfies(summary -> {
                    assertThat(summary.getRainStatus()).isEqualTo("RAIN_AND_SNOW");
                    assertThat(summary.getMinTemp()).isEqualTo(-1);
                    assertThat(summary.getMaxTemp()).isEqualTo(2);
                });
    }

    @Test
    void 오늘_0시부터_내일_0시까지_경계값_테스트() {

        // Given
        LocalDateTime now = LocalDateTime.now();
        Location location = createAndPersistLocation();

        LocalDateTime endOfToday = now.plusDays(1).with(LocalTime.MIDNIGHT);

        createAndPersistWeather(location, LocalDateTime.now().plusMinutes(1), 5, RainType.SNOW);
        createAndPersistWeather(location, endOfToday, 10, RainType.RAIN);

        // When
        List<WeatherSummaryDTO> result = weatherRepository.findWeatherSummary(Set.of(location.getId()));

        assertThat(result)
                .hasSize(1)
                .first()
                .satisfies(summary -> {
                    assertThat(summary.getRainStatus()).isEqualTo("RAIN_AND_SNOW");
                    assertThat(result.get(0).getMinTemp()).isEqualTo(5);
                    assertThat(result.get(0).getMaxTemp()).isEqualTo(10);
                });
    }

    @ParameterizedTest
    @MethodSource("provideWeather")
    void 그외의_저장된_날씨에_따라_최고저_기온과_강수량_리턴(String expectedRainType, List<Integer> temperatures, List<RainType> rainTypes) {

        // Given
        LocalDateTime now = LocalDateTime.now();
        Location location = createAndPersistLocation();

        IntStream.range(0, temperatures.size())
                .forEach(i -> createAndPersistWeather(location, now.plusHours(i + 1), temperatures.get(i), rainTypes.get(i)));

        Set<Long> locationIds = Collections.singleton(location.getId());

        // When
        List<WeatherSummaryDTO> result = weatherRepository.findWeatherSummary(locationIds);

        // Then
        assertThat(result)
                .hasSize(1)
                .first()
                .satisfies(summary -> {
                    assertThat(summary.getMaxTemp()).isEqualTo(Collections.max(temperatures));
                    assertThat(summary.getMinTemp()).isEqualTo(Collections.min(temperatures));
                    assertThat(summary.getRainStatus()).isEqualTo(expectedRainType); // CaseBuilder otherwise 로직 검증
                });
    }

    static Stream<Arguments> provideWeather() {
        return Stream.of(
                arguments(RainType.RAIN.toString(), List.of(10,5,8) ,List.of(RainType.RAIN,RainType.RAIN,RainType.RAIN)),
                arguments(RainType.NONE.toString(), List.of(-5,5,8) ,List.of(RainType.NONE,RainType.NONE,RainType.NONE)),
                arguments(RainType.SNOW.toString(), List.of(-1,15,28) ,List.of(RainType.SNOW,RainType.SNOW,RainType.SNOW)),
                arguments(RainType.SNOW.toString(), List.of(23,5,-8) ,List.of(RainType.SNOW,RainType.NONE,RainType.SNOW)),
                arguments(RainType.RAIN_AND_SNOW.toString(), List.of(0,25,-8) ,List.of(RainType.RAIN,RainType.SNOW,RainType.RAIN)),
                arguments(RainType.RAIN_AND_SNOW.toString(), List.of(-5,-1,-9) ,List.of(RainType.RAIN_AND_SNOW,RainType.SNOW,RainType.RAIN)));
    }

    private Location createAndPersistLocation() {
        Location location = Location.builder()
                .build();

        return entityManager.persistAndFlush(location); // 저장 및 즉시 DB 반영
    }

    private void createAndPersistWeather(Location location, LocalDateTime presentationTime, Integer temperature, RainType rainType) {

        Weather weather = Weather.builder()
                .location(location)
                .presentationTime(presentationTime)
                .temperature(temperature)
                .humidity(50)
                .windSpeed(1.5)
                .rainType(rainType).build();

        entityManager.persistAndFlush(weather);
    }
}


