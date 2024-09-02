package org.pknu.weather.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pknu.weather.domain.Member;
import org.pknu.weather.domain.Post;
import org.pknu.weather.dto.PostResponse;
import org.pknu.weather.dto.converter.PostConverter;
import org.pknu.weather.dto.converter.PostResponseConverter;
import org.pknu.weather.repository.MemberRepository;
import org.pknu.weather.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true, propagation = Propagation.NESTED)
public class PostQueryService {
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final PostService postService;

    public PostResponse.PostList getPosts(Long memberId, Long lastPostId, Long size) {
        Member member = memberRepository.safeFindById(memberId);
        List<Post> postList = postService.getPosts(memberId, lastPostId, size);
        return PostResponseConverter.toPostList(member, postList, postList.size() > size);
    }

    public List<PostResponse.Post> getPopularPosts(Long memberId) {
        Member member = memberRepository.safeFindById(memberId);
        List<Post> popularPostList = postRepository.getPopularPostList(member.getLocation());
        return PostResponseConverter.toPopularPostList(member, popularPostList);
    }
}
