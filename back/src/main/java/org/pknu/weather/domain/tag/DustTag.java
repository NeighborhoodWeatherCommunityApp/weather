package org.pknu.weather.domain.tag;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DustTag implements EnumTag {
    VERY_GOOD("매우", "좋음", 0),
    GOOD("", "좋음", 1),
    NORMAL("", "보통", 2),
    LITTLE_BAD("약간", "나쁨", 3),
    VERY_BAD("매우", "나쁨", 4)
    ;

    private final String adverb;
    private final String text;
    private final Integer code;

    @Override
    public String toString() {
        String string = getAdverb() + " " + getText();
        return string.trim();
    }

    @Override
    public EnumTag findByCode(int code) {
        for(DustTag tag : DustTag.values()) {
            if(tag.getCode().equals(code))
                return tag;
        }

        return DustTag.VERY_GOOD;
    }
}

