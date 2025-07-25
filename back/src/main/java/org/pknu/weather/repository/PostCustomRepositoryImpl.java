package org.pknu.weather.repository;

import static org.pknu.weather.domain.QLocation.location;
import static org.pknu.weather.domain.QPost.post;
import static org.pknu.weather.domain.QRecommendation.recommendation;
import static org.pknu.weather.member.entity.QMember.member;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.pknu.weather.common.BoundingBox;
import org.pknu.weather.domain.Location;
import org.pknu.weather.domain.Post;
import org.pknu.weather.domain.common.PostType;


@RequiredArgsConstructor
public class PostCustomRepositoryImpl implements PostCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;

    /**
     * post 무한 스크롤, 이내의 거리에 있는 사용자가 작성한 post만 보여줌 정렬은 최신순이며 24시간 이내의 게시글만 가져옴
     *
     * @param lastPostId     마지막 post id
     * @param size           가져올 post의 개수
     * @param locationEntity Location entity
     * @param postType
     * @return size + 1 개의 Post
     */
    public List<Post> findAllWithinDistance(Long lastPostId, Long size, Location locationEntity, PostType postType) {
        BoundingBox box = BoundingBox.calculateBoundingBox(locationEntity);
        LocalDateTime yesterday = LocalDateTime.now().minusHours(24);

        return jpaQueryFactory.selectFrom(post)
                .join(post.location, location).fetchJoin()
                .join(post.member, member).fetchJoin()
                .where(
                        location.latitude.between(box.getLeftLat(), box.getRightLat()),
                        location.longitude.between(box.getLeftLon(), box.getRightLon()),
                        goeLastPostId(lastPostId),
//                        post.postType.eq(postType),
                        post.content.isNotEmpty(),
                        post.createdAt.after(yesterday)
                )
                .orderBy(
                        post.createdAt.desc()
                )
                .limit(size + 1)
                .fetch();
    }

    private BooleanExpression goeLastPostId(Long lastPostId) {
        return post.id.goe(lastPostId);
    }


    @Override
    public Integer countTodayPostByMemberId(Long memberId) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = now.withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime end = start.plusDays(1);

        return jpaQueryFactory
                .selectFrom(post)
                .where(
                        post.member.id.eq(memberId),
                        post.createdAt.after(start),
                        post.createdAt.before(end)
                )
                .fetch()
                .size();
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
}
