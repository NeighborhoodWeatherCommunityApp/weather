package org.pknu.weather.post.repository;

import java.util.List;
import org.pknu.weather.location.entity.Location;
import org.pknu.weather.post.entity.Post;
import org.pknu.weather.post.enums.PostType;

public interface PostCustomRepository {
    List<Post> findAllWithinDistance(Long lastPostId, Long size, Location location, PostType postType);

    List<Post> getPopularPostList(Location location);

    Integer countTodayPostByMemberId(Long memberId);
}
