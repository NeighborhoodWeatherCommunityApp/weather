package org.pknu.weather.domain.exp;

public interface ExpRewardLimitPolicy {
    boolean canReward(Long memberId);
}
