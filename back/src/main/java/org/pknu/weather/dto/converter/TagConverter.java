package org.pknu.weather.dto.converter;

import lombok.RequiredArgsConstructor;
import org.pknu.weather.domain.Tag;
import org.pknu.weather.domain.tag.EnumTag;
import org.pknu.weather.dto.PostRequest;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class TagConverter {
    public EnumTag toTagFromCode(int code, Class<? extends EnumTag> enumTag) {
        EnumTag tag = Arrays.stream(enumTag.getEnumConstants()).toList().get(0);
        return tag.findByCode(code);
    }

//    public static Tag toTag(PostRequest.CreatePost createPost) {
//        return Tag.builder()
//                .temperTag(createPost.getTemperatureTag().toString())
//                .skyTag(createPost.getSkyTag().toString())
//                .humidityTag(createPost.getHumidityTag().toString())
//                .windTag(createPost.getWindTag().toString())
//                .dustTag(createPost.getDustTag().toString())
//                .build();
//    }

    public static Tag toTag(PostRequest.CreatePost createPost) {
        return Tag.builder()
                .temperTag(createPost.getTemperatureTag())
                .skyTag(createPost.getSkyTag())
                .humidityTag(createPost.getHumidityTag())
                .windTag(createPost.getWindTag())
                .dustTag(createPost.getDustTag())
                .build();
    }
}
