package org.pknu.weather.recomandation.converter;

import org.pknu.weather.member.entity.Member;
import org.pknu.weather.post.entity.Post;
import org.pknu.weather.recomandation.entity.Recommendation;

public class RecommendationConverter {

    public static Recommendation toRecommendation(Member member, Post post) {
        return Recommendation.builder()
                .member(member)
                .post(post)
                .build();
    }
}
