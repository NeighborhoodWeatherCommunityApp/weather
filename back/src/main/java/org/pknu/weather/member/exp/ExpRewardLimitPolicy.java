package org.pknu.weather.member.exp;

public interface ExpRewardLimitPolicy {
    boolean canReward(Long memberId);
}
