package org.pknu.weather.dto.converter;

import lombok.RequiredArgsConstructor;
import org.pknu.weather.domain.tag.*;
import org.pknu.weather.dto.PostRequest;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostRequestConverter {
    private final TagConverter tagConverter;

    public PostRequest.CreatePost toCreatePost(PostRequest.Params params) {
        return PostRequest.CreatePost.builder()
                .content(params.getContent())
                .temperatureTag((TemperatureTag) tagConverter.toTagFromCode(params.getTemperatureTagCode(), TemperatureTag.class))
                .skyTag((SkyTag) tagConverter.toTagFromCode(params.getSkyTagCode(), SkyTag.class))
                .windTag((WindTag) tagConverter.toTagFromCode(params.getWindTagCode(), WindTag.class))
                .humidityTag((HumidityTag) tagConverter.toTagFromCode(params.getHumidityTagCode(), HumidityTag.class))
                .dustTag((DustTag) tagConverter.toTagFromCode(params.getDustTagCode(), DustTag.class))
                .build();
    }
}
