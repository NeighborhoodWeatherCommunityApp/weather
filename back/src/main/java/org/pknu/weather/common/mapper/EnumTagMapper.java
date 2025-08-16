package org.pknu.weather.common.mapper;

import org.pknu.weather.post.tag.enums.EnumTag;
import org.pknu.weather.post.dto.TagDto;

import java.util.*;
import java.util.stream.Collectors;

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
            dtoList.sort(Comparator.comparingInt(TagDto::getCode));
        });

        return list;
    }
}
