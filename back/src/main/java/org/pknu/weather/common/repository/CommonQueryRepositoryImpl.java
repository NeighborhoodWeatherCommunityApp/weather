package org.pknu.weather.common.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CommonQueryRepositoryImpl implements  CommonQueryRepository {
    private final JPAQueryFactory jpaQueryFactory;

//    public void getSimpleWeatherInfo(Location locationEntity) {
//        List<Location> nearbyLocations = jpaQueryFactory
//                .select(Construct)
//                .from(location)
//                .leftJoin(location.weatherList, weather)
//                .leftJoin(location.TagList, tag)
//                .where(
//                        QueryUtils.isContains(locationEntity)
//                )
//                .fetch();
//    }
}
