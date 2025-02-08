package org.pknu.weather.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.EnumPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.pknu.weather.common.BoundingBox;
import org.pknu.weather.domain.Location;
import org.pknu.weather.domain.tag.EnumTag;
import org.pknu.weather.dto.TagQueryResult;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.pknu.weather.domain.QLocation.location;
import static org.pknu.weather.domain.QTag.tag;

@RequiredArgsConstructor
public class TagCustomRepositoryImpl implements TagCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public List<TagQueryResult> rankingTags(Location locationEntity) {
        List<TagQueryResult> tagQueryResultList = new ArrayList<>();

        tagQueryResultList.add(getTagTuple(locationEntity, tag.temperTag));
        tagQueryResultList.add(getTagTuple(locationEntity, tag.windTag));
        tagQueryResultList.add(getTagTuple(locationEntity, tag.humidityTag));
        tagQueryResultList.add(getTagTuple(locationEntity, tag.skyTag));
        tagQueryResultList.add(getTagTuple(locationEntity, tag.dustTag));

        return tagQueryResultList.stream()
                .sorted((o1, o2) -> Math.toIntExact(o1.getCount() - o2.getCount()))
                .toList();
    }

    private TagQueryResult getTagTuple(Location locationEntity, EnumPath<? extends EnumTag> pTag) {
        BoundingBox box = BoundingBox.calculateBoundingBox(locationEntity);
        LocalDateTime threeHoursAgo = LocalDateTime.now().minusHours(3);

        Tuple tuple = jpaQueryFactory
                .select(pTag.count(), pTag)
                .from(tag)
                .join(tag.location, location)
                .where(
                        location.latitude.between(box.getLeftLat(), box.getRightLat()),
                        location.longitude.between(box.getLeftLon(), box.getRightLon()),
                        tag.createdAt.after(threeHoursAgo)
                )
                .groupBy(pTag)
                .orderBy(pTag.count().desc())
                .fetchFirst();

        assert tuple != null;
        return TagQueryResult.builder()
                .tag(tuple.get(pTag))
                .count(tuple.get(pTag.count()))
                .build();
    }
}
