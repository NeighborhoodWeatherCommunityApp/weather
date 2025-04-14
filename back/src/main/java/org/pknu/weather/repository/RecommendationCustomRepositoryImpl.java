package org.pknu.weather.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.pknu.weather.domain.Member;
import org.pknu.weather.domain.Recommendation;

import java.time.LocalDateTime;

import static org.pknu.weather.domain.QRecommendation.recommendation;

@RequiredArgsConstructor
public class RecommendationCustomRepositoryImpl implements RecommendationCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;

    /**
     * 사용자가 게시글에 좋아요를 누를 수 있는지 여부를 확인하니다.
     *
     * @param memberId
     * @param postId
     * @return 좋아요를 누를 수 있으면 true, 누를 수 없으면 false
     */
    @Override
    public Boolean isRecommend(Long memberId, Long postId) {
        Recommendation fetched = jpaQueryFactory
                .selectFrom(recommendation)
                .where(
                        recommendation.post.id.eq(postId),
                        recommendation.member.id.eq(memberId)
                )
                .fetchOne();

        return fetched == null || !fetched.getDeleted();
    }

    @Override
    public int countTodayRecommendationByMemberId(Long memberId) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = now.withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime end = start.plusDays(1);

        return jpaQueryFactory
                .selectFrom(recommendation)
                .where(
                        recommendation.member.id.eq(memberId),
                        recommendation.createdAt.after(start),
                        recommendation.createdAt.before(end)
                )
                .fetch()
                .size();
    }

    @Override
    public void softDeleteByMemberAndPostId(Member member, Long postId) {
        jpaQueryFactory
                .update(recommendation)
                .set(recommendation.deleted, true)
                .where(
                        recommendation.post.id.eq(postId),
                        recommendation.member.id.eq(member.getId())
                )
                .execute();
    }
}
