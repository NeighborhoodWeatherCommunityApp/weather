package org.pknu.weather.post.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.pknu.weather.tag.enums.EnumTag;

@Getter
public class TagDto {
    private final String key;
    private final String text;
    private final Integer code;

    @Builder
    public TagDto(EnumTag enumTag) {
        key = enumTag.getKey();
        text = enumTag.toText();
        code = enumTag.getCode();
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SimpleTag {
        private String text;
    }
}
