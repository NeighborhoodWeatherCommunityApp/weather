package org.pknu.weather.domain.tag;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum HumidityTag implements Taggable {
    DRY("", "건조함", 0),
    NORMAL("", "보통", 1),
    LITTLE_HUMID("약간", "습함", 3),
    HUMID("", "습함", 3),
    VERY_HUMID("매우", "습함", 4)
    ;

    private final String adverb;
    private final String text;
    private final Integer code;

    @Override
    public Taggable fromCode(int code) {
        for(HumidityTag tag : HumidityTag.values()) {
            if(tag.getCode().equals(code))
                return tag;
        }

        return HumidityTag.NORMAL;
    }
}
