package org.pknu.weather.common.mapper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.pknu.weather.domain.tag.EnumTag;
import org.pknu.weather.dto.TagDto;

public class EnumTagMapper {
    private final Map<String, EnumTag> map = new HashMap<>();

    private List<EnumTag> toEnumTagValues(Class<? extends EnumTag> e) {
        return Arrays
                .stream(e.getEnumConstants())
                .collect(Collectors.toList());
    }

    private void isDuplicateTagKeyName(String tagKeyName) {
        if (map.containsKey(tagKeyName)) {
            throw new IllegalArgumentException("EnumTag의 Key가 중복입니다.: " + tagKeyName);
        }
    }

    public void put(Class<? extends EnumTag> e) {
        List<EnumTag> tagValues = toEnumTagValues(e);

        for (EnumTag tagValue : tagValues) {
            isDuplicateTagKeyName(tagValue.getKey());
            map.put(tagValue.getKey(), tagValue);
        }
    }

    public EnumTag get(String key) {
        return map.get(key);
    }

    public Map<String, EnumTag> getAll() {
        return map;
    }

    public Map<String, List<TagDto>> getAllDto() {
        Map<String, List<TagDto>> list = new HashMap<>();

        map.values().forEach(tag -> {
            TagDto tagDto = new TagDto(tag);
            String tagName = tag.getTagName();

            if (!list.containsKey(tagName)) {
                list.put(tagName, new LinkedList<>());
            }

            list.get(tagName).add(tagDto);
        });

        list.forEach((s, dtoList) -> {
            dtoList.sort((o1, o2) -> o1.getCode() - o2.getCode());
        });

        return list;
    }
}
