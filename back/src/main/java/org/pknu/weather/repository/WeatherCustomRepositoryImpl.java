package org.pknu.weather.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.pknu.weather.common.formatter.DateTimeFormatter;
import org.pknu.weather.common.utils.QueryUtils;
import org.pknu.weather.domain.Location;
import org.pknu.weather.domain.Weather;
import org.pknu.weather.dto.WeatherQueryResult;

import java.time.LocalDateTime;

import static org.pknu.weather.domain.QWeather.weather;

@RequiredArgsConstructor
public class WeatherCustomRepositoryImpl implements WeatherCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;


    /**
     * 24시간 이내에 비소식이 있는지 확인합니다.
     *
     * @param locationEntity
     * @return 비소식이 없으면 null을 반환.
     */
    public WeatherQueryResult.SimpleRainInfo getSimpleRainInfo(Location locationEntity) {
        return jpaQueryFactory
                .select(Projections.constructor(WeatherQueryResult.SimpleRainInfo.class,
                        weather.presentationTime,
                        weather.rainProb,
                        weather.rain
                ))
                .from(weather)
                .where(
                        QueryUtils.isSameLocation(locationEntity, weather),
                        QueryUtils.presentationTimeWithinLast24Hours(weather),
                        weather.rain.gt(0)
                )
                .fetchFirst();
    }

    /**
     * 해당 지역의 날씨가 갱신되었는지 확인하는 메서드
     * ex.
     * baseTime: 14:00, now: 14:00~16:59 true
     * baseTime: 14:00, now: 17:00~      false
     * @param location
     * @return true = 갱신되었음(3시간 안지남), false = 갱신되지 않았음(3시간 지남)
     */
    @Override
    public boolean weatherHasBeenUpdated(Location location) {
        LocalDateTime now = LocalDateTime.now()
                .withMinute(0)
                .withSecond(0)
                .withNano(0);

        LocalDateTime baseTime = DateTimeFormatter.getBaseTimeCloseToNow();

        LocalDateTime weatherBaseTime = jpaQueryFactory
                .select(weather.basetime)
                .from(weather)
                .where(
                        weather.presentationTime.after(now),
                        weather.location.eq(location)
                )
                .fetchFirst();

        assert weatherBaseTime != null;
        return weatherBaseTime.isEqual(baseTime);
    }

    /**
     * 해당 지역의 날씨 데이터가 존재하는지 확인하는 메서드
     *
     * @param location
     * @return true = 존재함, false = 존재 하지 않음
     */
    @Override
    public boolean weatherHasBeenCreated(Location location) {
        LocalDateTime now = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);

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
}
