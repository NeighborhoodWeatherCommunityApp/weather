package org.pknu.weather.dto.converter;

import org.pknu.weather.member.entity.Member;
import org.pknu.weather.domain.Post;
import org.pknu.weather.domain.Recommendation;

public class RecommendationConverter {

    public static Recommendation toRecommendation(Member member, Post post) {
        return Recommendation.builder()
                .member(member)
                .post(post)
                .build();
    }
}
