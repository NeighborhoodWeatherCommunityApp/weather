package org.pknu.weather.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pknu.weather.member.entity.Member;
import org.pknu.weather.domain.Post;
import org.pknu.weather.domain.common.PostType;
import org.pknu.weather.dto.PostResponse;
import org.pknu.weather.dto.converter.PostResponseConverter;
import org.pknu.weather.member.repository.MemberRepository;
import org.pknu.weather.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostQueryService {
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final PostService postService;

    public PostResponse.PostList getWeatherPosts(String email, Long lastPostId, Long size, String postType,
                                                 Long locationId) {
        Member member = memberRepository.safeFindByEmail(email);
        List<Post> postList = postService.getPosts(member.getId(), lastPostId, size, postType, locationId);
        return PostResponseConverter.toPostList(member, postList, postList.size() > size);
    }

    public List<PostResponse.Post> getLatestPostList(String email) {
        Member member = memberRepository.safeFindByEmail(email);
        List<Post> popularPostList = postRepository.findAllWithinDistance(1L, 5L, member.getLocation(),
                PostType.WEATHER);
        return PostResponseConverter.toLatestPostList(member, popularPostList);
    }
}
