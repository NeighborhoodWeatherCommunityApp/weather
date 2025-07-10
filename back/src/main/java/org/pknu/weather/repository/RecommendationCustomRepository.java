package org.pknu.weather.repository;

import org.pknu.weather.domain.Member;

public interface RecommendationCustomRepository {
    Boolean isRecommend(Long memberId, Long postId);

    int countTodayRecommendationByMemberId(Long memberId);
    void softDeleteByMemberAndPostId(Member member, Long postId);
}
