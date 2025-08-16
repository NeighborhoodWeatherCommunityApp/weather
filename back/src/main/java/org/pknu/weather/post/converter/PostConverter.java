package org.pknu.weather.post.converter;

import org.pknu.weather.member.entity.Member;
import org.pknu.weather.post.entity.Post;
import org.pknu.weather.post.tag.entity.Tag;
import org.pknu.weather.post.enums.PostType;
import org.pknu.weather.post.dto.PostRequest;

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
