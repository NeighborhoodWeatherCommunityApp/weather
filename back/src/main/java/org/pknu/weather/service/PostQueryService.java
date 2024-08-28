package org.pknu.weather.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pknu.weather.domain.Member;
import org.pknu.weather.domain.Post;
import org.pknu.weather.dto.PostResponse;
import org.pknu.weather.dto.converter.PostResponseConverter;
import org.pknu.weather.repository.MemberRepository;
import org.pknu.weather.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostQueryService {
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final PostService postService;

    public PostResponse.Posts getPosts(Long memberId, Long lastPostId, Long size) {
        Member member = memberRepository.safeFindById(memberId);
        List<Post> postList = postService.getPosts(memberId, lastPostId, size);
        return PostResponseConverter.toPosts(member, postList, postList.size() > size);
    }
}
