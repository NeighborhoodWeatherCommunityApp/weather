package org.pknu.weather.domain.tag;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RainTag implements Taggable {
    NOTHING("", "안옴", 0),
    ALMOST_NOTHING("거의", "안옴", 1),
    VERY_WEAK("매우", "약함", 2),
    WEAK("", "약함", 3),
    AVERAGE("", "보통", 4),
    STRONG("", "강함", 5),
    LITTLE_STRONG("조금", "강함", 6),
    VERY_STRONG("매우", "강함", 7);

    private final String Adverb;
    private final String text;
    private final Integer code;

    @Override
    public Taggable fromCode(int code) {
        for(RainTag tag : RainTag.values()) {
            if(tag.getCode().equals(code))
                return tag;
        }

        return RainTag.NOTHING;
    }
}
