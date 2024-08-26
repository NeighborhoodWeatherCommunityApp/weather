package org.pknu.weather.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.BooleanTemplate;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.pknu.weather.domain.Location;
import org.pknu.weather.domain.Post;

import java.util.List;

import static org.pknu.weather.domain.QLocation.location;
import static org.pknu.weather.domain.QMember.member;
import static org.pknu.weather.domain.QPost.post;


@RequiredArgsConstructor
public class PostCustomRepositoryImpl implements PostCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;

    /**
     * post 무한 스크롤, distance 이내의 거리에 있는 사용자가 작성한 post만 보여줌
     * 정렬은 최신순
     *
     * @param lastPostId 마지막 post id
     * @param size 가져올 post의 개수
     * @param loc Location entity
     * @param distance 거리 (m단위)
     * @return size + 1 개의 Post
     */
    public List<Post> findAllWithinDistance(Long lastPostId, Long size, Location loc, Integer distance) {
        Double latitude = loc.getLatitude();
        Double longitude = loc.getLongitude();

        return jpaQueryFactory.selectFrom(post)
                .join(post.location, location).fetchJoin()
                .join(post.member, member).fetchJoin()
//                .join(post.recommendation, recommendation).fetchJoin()
                .where(
                        goeLastPostId(lastPostId),
                        isContains(latitude, longitude, distance)
                )
                .orderBy(
                        post.id.desc()
                )
                .limit(size + 1)
                .fetch();
    }

    /**
     *
     * @param latitude 위도 (-90 ~ 90)
     * @param longitude 경도 (-180 ~ 180)
     * @param distance 거리 (m단위)
     * @return BooleanTemplate
     */
    private BooleanTemplate isContains(Double latitude, Double longitude, Integer distance) {
        String target = "Point(%f %f)".formatted(latitude, longitude);
        String geoFunction = "ST_CONTAINS(ST_BUFFER(ST_GeomFromText('%s', 4326), {0}), point)";
        String expression = String.format(geoFunction, target);

        return Expressions.booleanTemplate(expression, distance);
    }

    public BooleanExpression goeLastPostId(Long lastPostId) {
        return post.id.goe(lastPostId);
    }
}
