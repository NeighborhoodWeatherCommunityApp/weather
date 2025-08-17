package org.pknu.weather.config;

import java.util.Map;
import org.pknu.weather.member.exp.CreatePostExpRewardLimitPolicy;
import org.pknu.weather.member.exp.ExpEvent;
import org.pknu.weather.member.exp.ExpRewardLimitPolicy;
import org.pknu.weather.member.exp.RecommendationExpRewardLimitPolicy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExpRewardPolicyConfig {
    @Bean
    public Map<ExpEvent, ExpRewardLimitPolicy> policyMap(
            CreatePostExpRewardLimitPolicy createPostExpRewardLimitPolicy,
            RecommendationExpRewardLimitPolicy recommendationExpRewardLimitPolicy
    ) {
        return Map.of(
                ExpEvent.CREATE_POST, createPostExpRewardLimitPolicy,
                ExpEvent.RECOMMEND, recommendationExpRewardLimitPolicy
        );
    }
}
