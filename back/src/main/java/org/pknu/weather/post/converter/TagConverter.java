package org.pknu.weather.post.converter;

import org.pknu.weather.common.mapper.EnumTagMapper;
import org.pknu.weather.location.entity.Location;
import org.pknu.weather.post.tag.entity.Tag;
import org.pknu.weather.post.tag.enums.DustTag;
import org.pknu.weather.post.tag.enums.HumidityTag;
import org.pknu.weather.post.tag.enums.SkyTag;
import org.pknu.weather.post.tag.enums.TemperatureTag;
import org.pknu.weather.post.tag.enums.WindTag;
import org.pknu.weather.post.dto.PostRequest;

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
