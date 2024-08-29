package org.pknu.weather.common;

import org.pknu.weather.domain.Member;
import org.pknu.weather.domain.Recommendation;

import java.util.List;

public class RecommendationUtils {

    public static Integer likeCount(List<Recommendation> recommendationList) {
        return recommendationList.size();
    }

    public static Boolean isClickable(List<Recommendation> recommendationList, Member member) {
        return recommendationList.stream()
                .noneMatch(recommendation -> recommendation.getMember().getId().equals(member.getId()));
    }
}
