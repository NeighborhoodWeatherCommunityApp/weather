package org.pknu.weather.domain.tag;

import org.pknu.weather.weather.dto.TotalWeatherDTO;

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

    EnumTag weatherValueToTag(TotalWeatherDTO totalWeatherDto);

    default Boolean tagSelectedCheck(EnumTag tag, TotalWeatherDTO totalWeatherDto) {
        return tag == tag.weatherValueToTag(totalWeatherDto);
    }
}
