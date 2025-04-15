package org.pknu.weather.domain.exp;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ExpEvent {
    CREATE_POST("게시글 작성", 10L, false),
    ATTENDANCE("출석 체크", 1L, true),
    STREAK_7_DAYS("7일 연속 출석 체크", 15L, false),
    RECOMMEND("좋아요 클릭", 1L, false),
    RECOMMENDED("좋아요 받음", 3L, false),
    SHARE_KAKAO("카카오톡 날씨 공유", 5L, true),
    INACTIVE_7_DAYS("7일 미출석", -5L, false),
    INACTIVE_30_DAYS("30일 미출석", -25L, false);

    private final String rewardName;
    private final Long rewardExpAmount;
    private final Boolean allowApiRequest;
}
