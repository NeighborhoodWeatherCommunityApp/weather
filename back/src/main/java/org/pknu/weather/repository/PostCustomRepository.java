package org.pknu.weather.repository;

import java.util.List;
import org.pknu.weather.domain.Location;
import org.pknu.weather.domain.Post;
import org.pknu.weather.domain.common.PostType;

public interface PostCustomRepository {
    List<Post> findAllWithinDistance(Long lastPostId, Long size, Location location, PostType postType);

    List<Post> getPopularPostList(Location location);

    Integer countTodayPostByMemberId(Long memberId);
}
