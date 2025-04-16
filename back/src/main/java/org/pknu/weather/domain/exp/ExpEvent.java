package org.pknu.weather.domain.exp;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ExpEvent {
    CREATE_POST("게시글 작성", 10L),
    ATTENDANCE("출석 체크", 1L),
    STREAK_7_DAYS("7일 연속 출석 체크", 15L),
    RECOMMEND("좋아요 클릭", 1L),
    RECOMMENDED("좋아요 받음", 3L),
    SHARE_KAKAO("카카오톡 날씨 공유", 5L),
    INACTIVE_7_DAYS("7일 미출석", -5L),
    INACTIVE_30_DAYS("30일 미출석", -25L);

    private final String rewardName;
    private final Long rewardExpAmount;
}
