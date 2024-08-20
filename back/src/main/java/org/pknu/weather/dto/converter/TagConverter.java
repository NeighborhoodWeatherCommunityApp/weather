package org.pknu.weather.dto.converter;

import lombok.RequiredArgsConstructor;
import org.pknu.weather.domain.Tag;
import org.pknu.weather.domain.tag.Taggable;
import org.pknu.weather.dto.PostRequest;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TagConverter {
    private final Taggable tag;

    public Taggable toTagFromCode(int code) {
        return tag.fromCode(code);
    }

    public static Tag toTag(PostRequest.CreatePost createPost) {
        return Tag.builder()
                .temperTag(createPost.getTemperatureTag().toString())
                .skyTag(createPost.getSkyTag().toString())
                .humidityTag(createPost.getHumidityTag().toString())
                .windTag(createPost.getWindTag().toString())
                .dustTag(createPost.getDustTag().toString())
                .build();
    }
}
