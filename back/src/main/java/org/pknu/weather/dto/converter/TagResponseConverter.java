package org.pknu.weather.dto.converter;

import org.pknu.weather.domain.tag.EnumTag;
import org.pknu.weather.dto.TagDto;
import org.pknu.weather.dto.TagWithSelectedOrNotDto;
import org.pknu.weather.dto.TotalWeatherDto;

public class TagResponseConverter {

    public static TagDto.SimpleTag toSimpleTag(String text) {
        return TagDto.SimpleTag.builder()
                .text(text)
                .build();
    }

    public static TagWithSelectedOrNotDto toTagSelectedOrNotDto(EnumTag tag, TotalWeatherDto totalWeatherDto) {
        return TagWithSelectedOrNotDto.builder()
                .enumTag(tag)
                .selected(tag.tagSelectedCheck(tag, totalWeatherDto))
                .build();
    }

}
