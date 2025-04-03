package org.pknu.weather.repository;

public interface RecommendationCustomRepository {
    Boolean isRecommended(Long memberId, Long postId);

    int countTodayRecommendationByMemberId(Long memberId);
}
