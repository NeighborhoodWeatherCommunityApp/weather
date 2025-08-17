package org.pknu.weather.weather.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.*;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pknu.weather.common.formatter.DateTimeFormatter;
import org.pknu.weather.common.utils.QueryUtils;
import org.pknu.weather.location.entity.Location;
import org.pknu.weather.location.entity.QLocation;
import org.pknu.weather.weather.QWeather;
import org.pknu.weather.weather.Weather;
import org.pknu.weather.weather.dto.WeatherQueryResultDTO;
import org.pknu.weather.weather.dto.WeatherSummaryDTO;
import org.pknu.weather.weather.enums.RainType;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.pknu.weather.weather.QWeather.weather;

@Slf4j
@RequiredArgsConstructor
public class WeatherCustomRepositoryImpl implements WeatherCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;
    private final JdbcTemplate jdbcTemplate;
    private final EntityManager em;


    /**
     * 24시간 이내에 비소식이 있는지 확인합니다.
     *
     * @param locationEntity
     * @return 비소식이 없으면 null을 반환.
     */
    public WeatherQueryResultDTO.SimpleRainInfo getSimpleRainInfo(Location locationEntity) {
        return jpaQueryFactory
                .select(Projections.constructor(WeatherQueryResultDTO.SimpleRainInfo.class,
                        weather.presentationTime,
                        weather.rainProb,
                        weather.rain,
                        weather.snowCover
                ))
                .from(weather)
                .where(
                        QueryUtils.isSameLocation(locationEntity, weather),
                        QueryUtils.presentationTimeWithinLast24Hours(weather),
                        weather.rain.gt(0).or(weather.snowCover.gt(0))
                )
                .fetchFirst();
    }

    /**
     * 해당 지역의 날씨가 갱신되었는지 확인하는 메서드 ex. baseTime: 14:00, now: 14:00~16:59 true baseTime: 14:00, now: 17:00~      false
     *
     * @param location
     * @return true = 갱신되었음(3시간 안지남), false = 갱신되지 않았음(3시간 지남)
     */
    @Override
    public boolean weatherHasBeenUpdated(Location location) {
        LocalDateTime now = LocalDateTime.now()
                .withMinute(15)
                .withSecond(0)
                .withNano(0);

        LocalDateTime baseTime = DateTimeFormatter.getBaseLocalDateTime(LocalDateTime.now());

        LocalDateTime weatherBaseTime = jpaQueryFactory
                .select(weather.basetime)
                .from(weather)
                .where(
                        weather.presentationTime.after(now),
                        weather.location.eq(location)
                )
                .fetchFirst();

        return weatherBaseTime != null && weatherBaseTime.isEqual(baseTime);
    }

    /**
     * 해당 지역의 날씨 데이터가 존재하는지 확인하는 메서드
     *
     * @param location
     * @return true = 존재함, false = 존재 하지 않음
     */
    @Override
    public boolean weatherHasBeenCreated(Location location) {
        LocalDateTime now = LocalDateTime.now()
                .withMinute(15)
                .withSecond(0)
                .withNano(0);

        Weather w = jpaQueryFactory
                .select(weather)
                .from(weather)
                .where(
                        weather.presentationTime.after(now),
                        weather.location.eq(location)
                )
                .fetchFirst();

        return w != null;
    }

    @Override
    public Optional<Weather> findWeatherByClosestPresentationTime(Location location) {
        LocalDateTime now = LocalDateTime.now().plusHours(1).withMinute(0).withSecond(0).withNano(0);

        return Optional.ofNullable(jpaQueryFactory
                .selectFrom(weather)
                .where(
                        weather.location.eq(location),
                        weather.presentationTime.eq(now)
                )
                .fetchOne());
    }

    /**
     * 특정 지역의 날씨 예보중 현재 시각 이후의 예보들을 가져온다.
     *
     * @param locationEntity
     */
    @Override
    public Map<LocalDateTime, Weather> findAllByLocationAfterNow(Location locationEntity) {
        LocalDateTime now = LocalDateTime.now();

        List<Weather> weatherList = jpaQueryFactory
                .select(weather)
                .from(weather)
                .where(
                        weather.location.id.eq(locationEntity.getId()),
                        weather.presentationTime.after(now)
                )
                .fetch();

        return weatherList.stream()
                .collect((Collectors.toMap(Weather::getPresentationTime,
                        weather -> weather,
                        (existing, replacement) -> existing)));
    }

    @Override
    public List<WeatherSummaryDTO> findWeatherSummary(Set<Long> locationIds) {

        QWeather weather = QWeather.weather;
        QLocation location = QLocation.location;

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endDateTime = now.toLocalDate().plusDays(1).atStartOfDay();

        BooleanExpression isRainOrShower = weather.rainType.in(RainType.RAIN, RainType.SHOWER);
        BooleanExpression isSnow = weather.rainType.eq(RainType.SNOW);
        BooleanExpression isRainAndSnow = weather.rainType.eq(RainType.RAIN_AND_SNOW);
        BooleanExpression isNotNone = weather.rainType.ne(RainType.NONE);

        NumberTemplate<Integer> rainCount = Expressions.numberTemplate(Integer.class,
                "SUM(CASE WHEN {0} THEN 1 ELSE 0 END)", isRainOrShower);
        NumberTemplate<Integer> snowCount = Expressions.numberTemplate(Integer.class,
                "SUM(CASE WHEN {0} THEN 1 ELSE 0 END)", isSnow);
        NumberTemplate<Integer> rainAndSnowCount = Expressions.numberTemplate(Integer.class,
                "SUM(CASE WHEN {0} THEN 1 ELSE 0 END)", isRainAndSnow);
        NumberTemplate<Integer> notNoneCount = Expressions.numberTemplate(Integer.class,
                "SUM(CASE WHEN {0} THEN 1 ELSE 0 END)", isNotNone);

        StringExpression rainStatus = new CaseBuilder()
                .when(rainAndSnowCount.gt(0)).then("RAIN_AND_SNOW")
                .when(rainCount.gt(0).and(snowCount.eq(0))).then("RAIN")
                .when(snowCount.gt(0).and(rainCount.eq(0))).then("SNOW")
                .when(notNoneCount.eq(0)).then("NONE")
                .otherwise("RAIN_AND_SNOW");

        return jpaQueryFactory
                .select(Projections.fields(
                        WeatherSummaryDTO.class,
                        location.id.as("locationId"),
                        weather.temperature.max().as("maxTemp"),
                        weather.temperature.min().as("minTemp"),
                        rainStatus.as("rainStatus")
                ))
                .from(weather)
                .join(weather.location, location)
                .where(
                        weather.presentationTime.between(now, endDateTime)
                                .and(location.id.in(locationIds)))
                .groupBy(location.id)
                .fetch();
    }

    @Override
    public void batchSave(List<Weather> newForecast, Location location) {
        String query =
                "INSERT INTO weather(basetime, location_id, wind_speed, humidity, rain_prob, rain, rain_type, temperature, sensible_temperature, snow_cover, sky_type, presentation_time) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        batchUpdateWeathers(query, newForecast, location);
    }


    @Override
    public void batchUpdate(List<Weather> weatherList, Location location) {
        String query =
                "INSERT INTO weather(basetime, location_id, wind_speed, humidity, rain_prob, rain, rain_type, temperature, sensible_temperature, snow_cover, sky_type, presentation_time) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) "
                        + "ON DUPLICATE KEY UPDATE "
                        + "basetime = VALUES(basetime), wind_speed = VALUES(wind_speed), humidity = VALUES(humidity), rain_prob = VALUES(rain_prob), rain = VALUES(rain), rain_type = VALUES(rain_type), sensible_temperature = VALUES(sensible_temperature), snow_cover = VALUES(snow_cover), sky_type = VALUES(sky_type), presentation_time = VALUES(presentation_time)";
        batchUpdateWeathers(query, weatherList, location);
        em.clear();
    }

    private void batchUpdateWeathers(String query, List<Weather> forecast, Location location) {
        jdbcTemplate.batchUpdate(query,
                new BatchPreparedStatementSetter() {

                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        Weather w = forecast.get(i);
                        w.updateSensibleTemperature();

                        ps.setTimestamp(1, Timestamp.valueOf(w.getBasetime()));
                        ps.setLong(2, location.getId());
                        ps.setDouble(3, w.getWindSpeed());
                        ps.setInt(4, w.getHumidity());
                        ps.setInt(5, w.getRainProb());
                        ps.setFloat(6, w.getRain());
                        ps.setInt(7, w.getRainType().ordinal());
                        ps.setInt(8, w.getTemperature());
                        ps.setDouble(9, w.getSensibleTemperature());
                        ps.setFloat(10, w.getSnowCover());
                        ps.setInt(11, w.getSkyType().ordinal());
                        ps.setObject(12, w.getPresentationTime());
                    }

                    @Override
                    public int getBatchSize() {
                        if (forecast == null || forecast.isEmpty()) {
                            log.warn("빈 날씨 데이터 리스트를 입력함.");
                            return 0;
                        }

                        log.debug("Batch size: {}", forecast.size());
                        return forecast.size();
                    }
                });
    }

    /**
     * weather의 예보 시간이 24시간 이내인지 검사
     * ex) weather.presentationTIme이 01-01 00:00:00 ~ yyyy-MM-02 00:00:00 사이의 값
     * @param weather
     * @return
     */
    public static BooleanExpression presentationTimeWithinLast24Hours(QWeather weather) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime end = now.plusHours(24);

        return weather.presentationTime.between(now, end);
    }

    public static BooleanExpression isSameLocation(Location location, QWeather weather) {
        return weather.location.eq(location);
    }
}
