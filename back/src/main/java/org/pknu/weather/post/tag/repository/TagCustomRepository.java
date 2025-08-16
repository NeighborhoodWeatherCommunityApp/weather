package org.pknu.weather.post.tag.repository;

import org.pknu.weather.location.entity.Location;
import org.pknu.weather.post.dto.TagQueryResult;

import java.util.List;

public interface TagCustomRepository {
    List<TagQueryResult> rankingTags(Location locationEntity);
}
