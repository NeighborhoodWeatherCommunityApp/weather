package org.pknu.weather.recomandation.utils;

import java.util.List;
import org.pknu.weather.member.entity.Member;
import org.pknu.weather.recomandation.entity.Recommendation;

public class RecommendationUtils {

    public static Integer likeCount(List<Recommendation> recommendationList) {
        return Math.toIntExact(recommendationList.stream()
                .filter(Recommendation::isNotDeleted).count());
    }

    public static Boolean isClickable(List<Recommendation> recommendationList, Member postViewer) {
        return recommendationList.stream()
                .filter(Recommendation::isNotDeleted)
                .noneMatch(recommendation -> recommendation.getMember().equals(postViewer));
    }
}
