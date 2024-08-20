package org.pknu.weather.dto.converter;

import org.pknu.weather.domain.Location;
import org.pknu.weather.domain.Member;
import org.pknu.weather.domain.Post;
import org.pknu.weather.domain.Tag;
import org.pknu.weather.dto.PostRequest;

public class PostConverter {

    public static Post toPost(Member member, Location location, Tag tag, PostRequest.CreatePost createPost) {
        return Post.builder()
                .member(member)
                .location(location)
                .tag(tag)
                .content(createPost.getContent())
                .build();
    }
}
