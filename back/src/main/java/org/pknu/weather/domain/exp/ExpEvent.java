package org.pknu.weather.domain.exp;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ExpEvent {
    CREATE_POST(10L),   // 게시글 작성
    ATTENDANCE(1L),     // 출석체크
    STREAK_7_DAYS(15L), // 7연속 출석체크
    LIKE_GIVEN(1L),     // 좋아요 클릭
    LIKE_RECEIVED(3L),  // 좋아요 받음
    SHARE_KAKAO(5L),    // 카카오로 날씨 공유
    INACTIVE_7_DAYS(-5L),   // 7일 미출석
    INACTIVE_30_DAYS(-25L)  // 30일 미출석
    ;

    private final Long rewardExpAmount;
}
