package org.pknu.weather.recomandation.repository;

import org.pknu.weather.member.entity.Member;

public interface RecommendationCustomRepository {
    Boolean isRecommend(Long memberId, Long postId);

    int countTodayRecommendationByMemberId(Long memberId);
    void softDeleteByMemberAndPostId(Member member, Long postId);
}
