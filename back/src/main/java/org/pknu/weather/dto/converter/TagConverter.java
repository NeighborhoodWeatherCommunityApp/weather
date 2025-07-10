package org.pknu.weather.dto.converter;

import org.pknu.weather.common.mapper.EnumTagMapper;
import org.pknu.weather.domain.Location;
import org.pknu.weather.domain.Tag;
import org.pknu.weather.domain.tag.DustTag;
import org.pknu.weather.domain.tag.HumidityTag;
import org.pknu.weather.domain.tag.SkyTag;
import org.pknu.weather.domain.tag.TemperatureTag;
import org.pknu.weather.domain.tag.WindTag;
import org.pknu.weather.dto.PostRequest;

public class TagConverter {

    public static Tag toTag(PostRequest.CreatePost createPost, Location location) {
        return Tag.builder()
                .location(location)
                .temperTag(createPost.getTemperatureTag())
                .skyTag(createPost.getSkyTag())
                .humidityTag(createPost.getHumidityTag())
                .windTag(createPost.getWindTag())
                .dustTag(createPost.getDustTag())
                .build();
    }

    public static Tag toTag(PostRequest.CreatePostAndTagParameters parameters, Location location,
                            EnumTagMapper enumTagMapper) {
        return Tag.builder()
                .location(location)
                .temperTag((TemperatureTag) enumTagMapper.get(parameters.getTemperatureTagKey()))
                .skyTag((SkyTag) enumTagMapper.get(parameters.getSkyTagKey()))
                .humidityTag((HumidityTag) enumTagMapper.get(parameters.getHumidityTagKey()))
                .windTag((WindTag) enumTagMapper.get(parameters.getWindTagKey()))
                .dustTag((DustTag) enumTagMapper.get(parameters.getDustTagKey()))
                .build();
    }
}
