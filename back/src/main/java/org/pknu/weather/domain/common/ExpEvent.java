package org.pknu.weather.domain.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ExpEvent {
    CREATE_POST(10L),
    CLICK_LIKE(1L);

    private final Long rewardExpAmount;
}
