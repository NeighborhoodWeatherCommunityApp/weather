package org.pknu.weather.domain.tag;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TemperatureTag implements Taggable {
    VERY_COLD("", "추움", 0),
    COLD("", "추움", 1),
    LITTLE_COLD("조금", "추움", 2),
    COOL("", "선선", 3),
    NORMAL("", "보통", 4),
    WARM("", "따뜻", 5),
    LITTLE_WARM("조금", "따뜻", 6),
    LITTLE_HOT("조금", "더움", 7),
    HOT("", "더움", 8),
    VERY_HOT("매우", "더움", 9);

    private final String adverb;
    private final String text;
    private final Integer code;

    @Override
    public Taggable fromCode(int code) {
        for(TemperatureTag tag : TemperatureTag.values()) {
            if(tag.getCode().equals(code))
                return tag;
        }

        return TemperatureTag.NORMAL;
    }
}
