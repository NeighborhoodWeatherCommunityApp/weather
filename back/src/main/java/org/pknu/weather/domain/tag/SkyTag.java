package org.pknu.weather.domain.tag;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SkyTag implements Taggable {
    RAIN("비와요",  0),
    CLOUDY("흐려요",  1),
    CLEAR_AND_CLOUDY("맑고 구름이 많아요",  2),
    CLEAR("맑아요",  3),
    SUNNY("화창해요",  4)
    ;

    private final String text;
    private final Integer code;

    @Override
    public Taggable fromCode(int code) {
        for(SkyTag tag : SkyTag.values()) {
            if(tag.getCode().equals(code))
                return tag;
        }

        return SkyTag.CLEAR;
    }
}
