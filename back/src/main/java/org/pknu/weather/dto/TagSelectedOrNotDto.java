package org.pknu.weather.dto;

import lombok.Builder;
import lombok.Getter;
import org.pknu.weather.domain.tag.EnumTag;

@Getter
public class TagSelectedOrNotDto {
    private final String key;
    private final String text;
    private final Integer code;
    private final Boolean selected;

//    @Builder
//    public TagSelectedOrNotDto(TagDto tagDto, Boolean selected) {
//        key = tagDto.getKey();
//        text = tagDto.getText();
//        code = tagDto.getCode();
//        this.selected = false;
//    }

    @Builder
    public TagSelectedOrNotDto(EnumTag enumTag, Boolean selected) {
        key = enumTag.getKey();
        text = enumTag.toText();
        code = enumTag.getCode();
        this.selected = selected;
    }
}
