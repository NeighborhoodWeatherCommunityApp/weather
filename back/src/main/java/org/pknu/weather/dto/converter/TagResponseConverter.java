package org.pknu.weather.dto.converter;

import org.pknu.weather.domain.tag.EnumTag;
import org.pknu.weather.dto.TagDto;
import org.pknu.weather.dto.TagWithSelectedStatusDto;
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
