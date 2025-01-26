package org.pknu.weather.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.pknu.weather.common.BoundingBox;
import org.pknu.weather.domain.Location;
import org.pknu.weather.domain.Post;
import org.pknu.weather.domain.common.PostType;

import java.util.ArrayList;
import java.util.List;

import static org.pknu.weather.domain.QLocation.location;
import static org.pknu.weather.domain.QMember.member;
import static org.pknu.weather.domain.QPost.post;
import static org.pknu.weather.domain.QRecommendation.recommendation;


@RequiredArgsConstructor
public class PostCustomRepositoryImpl implements PostCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;

    /**
     * post 무한 스크롤,  이내의 거리에 있는 사용자가 작성한 post만 보여줌 정렬은 최신순
     *
     * @param lastPostId     마지막 post id
     * @param size           가져올 post의 개수
     * @param locationEntity Location entity
     * @param postType
     * @return size + 1 개의 Post
     */
    // TODO: tag 도 함께 페치 조인하여 n + 1 문제를 해결해야함(적용하니까 쿼리 결과가 0개 날라와서 일단 주석처리)
    public List<Post> findAllWithinDistance(Long lastPostId, Long size, Location locationEntity, PostType postType) {
        BoundingBox box = BoundingBox.calculateBoundingBox(locationEntity);

        return jpaQueryFactory.selectFrom(post)
                .join(post.location, location).fetchJoin()
                .join(post.member, member).fetchJoin()
//                .join(post.tag, tag)
                .where(
                        location.latitude.between(box.getLeftLat(), box.getRightLat()),
                        location.longitude.between(box.getLeftLon(), box.getRightLon()),
                        goeLastPostId(lastPostId),
                        post.postType.eq(postType),
                        post.content.isNotEmpty()
                )
                .orderBy(
                        post.id.desc()
                )
                .limit(size + 1)
                .fetch();
    }

    private BooleanExpression goeLastPostId(Long lastPostId) {
        return post.id.goe(lastPostId);
    }


    public List<Post> getPopularPostList(Location location) {
        StringPath likeCount = Expressions.stringPath("like_count");

        List<Tuple> tuples = jpaQueryFactory
                .select(post,
                        ExpressionUtils.as(
                                JPAExpressions
                                        .select(recommendation.count())
                                        .from(recommendation)
                                        .where(recommendation.post.eq(post)), "like_count")
                )
                .from(post)
                .orderBy(likeCount.desc())
                .limit(5)
                .fetch();

        List<Post> postList = new ArrayList<>();
        for (Tuple tuple : tuples) {
            Post p = tuple.get(post);
            postList.add(p);
        }

        return postList;
    }

    @Override
    public List<Post> getLatestPostList(Location location) {
        return jpaQueryFactory
                .select(post)
                .from(post)
                .orderBy(post.createdAt.desc())
                .where(post.content.isNotEmpty())
                .limit(5)
                .fetch();
    }
}
