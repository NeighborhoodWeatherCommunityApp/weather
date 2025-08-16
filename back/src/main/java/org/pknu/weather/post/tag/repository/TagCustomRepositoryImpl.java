package org.pknu.weather.post.tag.repository;

import static org.pknu.weather.post.tag.entity.QTag.tag;
import static org.pknu.weather.location.entity.QLocation.location;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.EnumPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.pknu.weather.apiPayload.code.status.ErrorStatus;
import org.pknu.weather.common.BoundingBox;
import org.pknu.weather.location.entity.Location;
import org.pknu.weather.post.tag.enums.EnumTag;
import org.pknu.weather.post.dto.TagQueryResult;
import org.pknu.weather.exception.GeneralException;

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

        if (tuple == null) {
            throw new GeneralException(ErrorStatus._TAG_NOT_FOUND);
        }

        return TagQueryResult.builder()
                .tag(tuple.get(pTag))
                .count(tuple.get(pTag.count()))
                .build();
    }
}
