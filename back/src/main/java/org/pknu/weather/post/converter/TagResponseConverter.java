package org.pknu.weather.post.converter;

import org.pknu.weather.post.tag.enums.EnumTag;
import org.pknu.weather.post.dto.TagDto;
import org.pknu.weather.post.dto.TagWithSelectedStatusDto;
import org.pknu.weather.weather.dto.TotalWeatherDto;

public class TagResponseConverter {

    public static TagDto.SimpleTag toSimpleTag(String text) {
        return TagDto.SimpleTag.builder()
                .text(text)
                .build();
    }

    public static TagWithSelectedStatusDto toTagSelectedOrNotDto(EnumTag tag, TotalWeatherDto totalWeatherDto) {
        return TagWithSelectedStatusDto.builder()
                .enumTag(tag)
                .selected(tag.tagSelectedCheck(tag, totalWeatherDto))
                .build();
    }

}
