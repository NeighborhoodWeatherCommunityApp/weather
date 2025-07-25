package org.pknu.weather.dto.converter;

import org.pknu.weather.member.entity.Member;
import org.pknu.weather.domain.Post;
import org.pknu.weather.domain.Tag;
import org.pknu.weather.domain.common.PostType;
import org.pknu.weather.dto.PostRequest;

public class PostConverter {

    public static Post toPost(Member member, Tag tag, String content) {
        return Post.builder()
                .member(member)
                .location(member.getLocation())
                .tag(tag)
                .content(content)
                .build();
    }

    public static Post toPost(Member member, String content) {
        return Post.builder()
                .member(member)
                .location(member.getLocation())
                .content(content)
                .build();
    }

    public static Post toPost(Member member, PostRequest.HobbyParams params) {
        return Post.builder()
                .member(member)
                .location(member.getLocation())
                .content(params.getContent())
                .postType(PostType.toPostType(params.getPostType()))
                .build();
    }

    public static Post toContentEmptyPost(Member member) {
        return Post.builder()
                .member(member)
                .location(member.getLocation())
                .content(null)
                .build();
    }
}
