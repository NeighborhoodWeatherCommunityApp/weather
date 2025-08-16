package org.pknu.weather.member.exp;

import lombok.RequiredArgsConstructor;
import org.pknu.weather.recomandation.repository.RecommendationRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RecommendationExpRewardLimitPolicy implements ExpRewardLimitPolicy {
    private final RecommendationRepository recommendationRepository;

    @Override
    public boolean canReward(Long memberId) {
        int DAILY_REWARD_LIMIT = 5;
        return recommendationRepository.countTodayRecommendationByMemberId(memberId) <= DAILY_REWARD_LIMIT;
    }
}
