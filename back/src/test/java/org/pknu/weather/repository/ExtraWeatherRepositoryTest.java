package org.pknu.weather.repository;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.pknu.weather.config.DataJpaTestConfig;
import org.pknu.weather.weather.ExtraWeather;
import org.pknu.weather.domain.Location;
import org.pknu.weather.weather.repository.ExtraWeatherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@Import(DataJpaTestConfig.class)
@DataJpaTest
class ExtraWeatherRepositoryTest {

    @Autowired
    private ExtraWeatherRepository extraWeatherRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Location location1;
    private Location location2;
    private ExtraWeather extraWeather_loc1;
    private ExtraWeather extraWeather_loc2;

    @BeforeEach
    void setUp() {
        entityManager.clear();
        entityManager.flush();

        location1 = createAndPersistLocation();
        location2 = createAndPersistLocation();

        LocalDateTime now = LocalDateTime.now();

        extraWeather_loc1 = createAndPersistExtraWeather(location1, now.minusHours(1));
        extraWeather_loc2 = createAndPersistExtraWeather(location2, now.minusHours(4));

    }

    @Test
    void location으로_extraWeather_조회_테스트() {
        // When
        Optional<ExtraWeather> foundExtraWeather = extraWeatherRepository.findByLocationId(location1.getId());

        //then
        assertThat(foundExtraWeather)
                .isPresent()
                .hasValueSatisfying(extraWeather -> {
                    assertThat(location1.getId()).isEqualTo(extraWeather.getLocation().getId());
                    assertThat(extraWeather_loc1.getId()).isEqualTo(extraWeather.getId());
                });

    }

    @Test
    void 잘못된_location으로_extraWeather_조회_테스트() {
        // Given
        Long nonExistingLocationId = 9999L;

        // When
        Optional<ExtraWeather> foundExtraWeather = extraWeatherRepository.findByLocationId(nonExistingLocationId);

        // Then
        assertThat(foundExtraWeather).isNotPresent();
    }


    @Test
    void 오래된_날씨_데이터_제외() {
        // Given
        Set<Long> targetLocationIds = new HashSet<>();
        targetLocationIds.add(location1.getId());
        targetLocationIds.add(location2.getId());

        LocalDateTime threeHoursAgo = LocalDateTime.now().minusHours(3);

        // When
        List<ExtraWeather> foundList = extraWeatherRepository.findExtraWeatherByLocations(targetLocationIds, threeHoursAgo);

        // then
        assertThat(foundList)
                .isNotNull()
                .hasSize(1)
                .extracting(ExtraWeather::getId)
                .containsExactly(extraWeather_loc1.getId())
                .doesNotContain(extraWeather_loc2.getId());
    }

    @Test
    void 잘못된_로케이션_기타_날씨_리스트_조회_테스트() {
        // Given
        Set<Long> targetLocationIds = new HashSet<>();
        targetLocationIds.add(9999L);

        LocalDateTime threeHoursAgo = LocalDateTime.now().minusHours(3);

        // When
        List<ExtraWeather> foundList = extraWeatherRepository.findExtraWeatherByLocations(targetLocationIds, threeHoursAgo);

        // Then
        assertThat(foundList).isNotNull().isEmpty();
    }

    private Location createAndPersistLocation() {
        Location location = Location.builder().build();
        return entityManager.persistAndFlush(location);
    }

    private ExtraWeather createAndPersistExtraWeather(Location location, LocalDateTime basetime) {
        ExtraWeather extraWeather = ExtraWeather.builder()
                .location(location)
                .basetime(basetime)
                .build();
        return entityManager.persistAndFlush(extraWeather);
    }
}