package org.pknu.weather.post.dto;

import lombok.*;
import org.pknu.weather.tag.enums.EnumTag;

@Getter
@Builder
@AllArgsConstructor
public class TagQueryResult {
    private final EnumTag tag;
    private final Long count;
}
