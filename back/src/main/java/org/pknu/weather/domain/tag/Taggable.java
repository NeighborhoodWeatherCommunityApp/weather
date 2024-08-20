package org.pknu.weather.domain.tag;

import org.springframework.stereotype.Component;

@Component
public interface Taggable {
    Taggable fromCode(int code);
}
