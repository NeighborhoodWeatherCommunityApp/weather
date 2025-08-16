package org.pknu.weather.post.converter;

import java.util.Arrays;
import org.pknu.weather.post.dto.PostRequest;
import org.pknu.weather.post.tag.enums.*;

public class PostRequestConverter {
    public static PostRequest.CreatePost toCreatePost(PostRequest.Params params) {
        return PostRequest.CreatePost.builder()
                .content(params.getContent())
                .temperatureTag((TemperatureTag) toTagFromCode(params.getTemperatureTagCode(), TemperatureTag.class))
                .skyTag((SkyTag) toTagFromCode(params.getSkyTagCode(), SkyTag.class))
                .windTag((WindTag) toTagFromCode(params.getWindTagCode(), WindTag.class))
                .humidityTag((HumidityTag) toTagFromCode(params.getHumidityTagCode(), HumidityTag.class))
                .dustTag((DustTag) toTagFromCode(params.getDustTagCode(), DustTag.class))
                .build();
    }

    public static EnumTag toTagFromCode(int code, Class<? extends EnumTag> enumTag) {
        EnumTag tag = Arrays.stream(enumTag.getEnumConstants()).toList().get(0);
        return tag.findByCode(code);
    }
}
