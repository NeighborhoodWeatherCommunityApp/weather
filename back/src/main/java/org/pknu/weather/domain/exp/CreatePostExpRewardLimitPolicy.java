package org.pknu.weather.domain.exp;

import lombok.RequiredArgsConstructor;
import org.pknu.weather.repository.PostRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreatePostExpRewardLimitPolicy implements ExpRewardLimitPolicy {
    private final PostRepository postRepository;

    @Override
    public boolean canReward(Long memberId) {
        int DAILY_REWARD_LIMIT = 3;
        return postRepository.countTodayPostByMemberId(memberId) <= DAILY_REWARD_LIMIT;
    }
}
