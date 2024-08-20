package org.pknu.weather.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pknu.weather.domain.Location;
import org.pknu.weather.domain.Member;
import org.pknu.weather.domain.Post;
import org.pknu.weather.domain.Tag;
import org.pknu.weather.dto.PostRequest;
import org.pknu.weather.dto.converter.PostConverter;
import org.pknu.weather.dto.converter.TagConverter;
import org.pknu.weather.repository.MemberRepository;
import org.pknu.weather.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void createPost(Long memberId, PostRequest.CreatePost createPost) {
        Member member = memberRepository.safeFindById(memberId);
        Location location = member.getLocation();
        Tag tag = TagConverter.toTag(createPost);
        Post post = PostConverter.toPost(member, location, tag, createPost);
    }
}
