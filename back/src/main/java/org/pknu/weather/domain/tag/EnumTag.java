package org.pknu.weather.domain.tag;

import org.pknu.weather.dto.TotalWeatherDto;

public interface EnumTag {
    EnumTag findByCode(int code);

    String getKey();

    String getText();

    Integer getCode();

    default String toText() {
        return (getAdverb() + " " + getText()).trim();
    }

    default String getAdverb() {
        return "";
    }

    default String getTagName() {
        String[] split = getClass().toString().split("\\.");
        return split[split.length - 1];
    }

    EnumTag weatherValueToTag(TotalWeatherDto totalWeatherDto);

    default Boolean tagSelectedCheck(EnumTag tag, TotalWeatherDto totalWeatherDto) {
        return tag == tag.weatherValueToTag(totalWeatherDto);
    }
}
