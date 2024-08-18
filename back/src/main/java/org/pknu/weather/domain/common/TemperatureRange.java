package org.pknu.weather.domain.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TemperatureRange {
    VERY_COLD("", "추움", 0),
    COLD("", "추움", 1),
    LITTLE_COLD("조금", "추움", 2),
    COOL("", "선선", 3),
    AVERAGE("", "보통", 4),
    WARM("", "따뜻", 5),
    LITTLE_WARM("조금", "따뜻", 6),
    LITTLE_HOT("조금", "더움", 7),
    HOT("", "더움", 8),
    VERY_HOT("매우", "더움", 9);

    private final String Adverb;
    private final String text;
    private final Integer code;
}
