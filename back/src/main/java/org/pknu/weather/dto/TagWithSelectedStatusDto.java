package org.pknu.weather.dto;

import lombok.Builder;
import lombok.Getter;
import org.pknu.weather.tag.enums.EnumTag;

@Getter
public class TagWithSelectedStatusDto {
    private final String key;
    private final String text;
    private final Integer code;
    private final Boolean selected;

//    @Builder
//    public TagWithSelectedStatusDto(TagDto tagDto, Boolean selected) {
//        key = tagDto.getKey();
//        text = tagDto.getText();
//        code = tagDto.getCode();
//        this.selected = false;
//    }

    @Builder
    public TagWithSelectedStatusDto(EnumTag enumTag, Boolean selected) {
        key = enumTag.getKey();
        text = enumTag.toText();
        code = enumTag.getCode();
        this.selected = selected;
    }
}
