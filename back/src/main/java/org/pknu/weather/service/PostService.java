package org.pknu.weather.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pknu.weather.domain.*;
import org.pknu.weather.dto.PostRequest;
import org.pknu.weather.dto.converter.PostConverter;
import org.pknu.weather.dto.converter.RecommendationConverter;
import org.pknu.weather.dto.converter.TagConverter;
import org.pknu.weather.repository.MemberRepository;
import org.pknu.weather.repository.PostRepository;
import org.pknu.weather.repository.RecommendationRepository;
import org.pknu.weather.repository.TagRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final RecommendationRepository recommendationRepository;
    private final TagRepository tagRepository;
    private static final int DISTANCE = 3000;

    @Transactional(readOnly = true)
    public List<Post> getPosts(Long memberId, Long lastPostId, Long size) {
        Member member = memberRepository.safeFindById(memberId);
        Location location = member.getLocation();
        List<Post> postList = postRepository.findAllWithinDistance(lastPostId, size, location, DISTANCE);

        return postList;
    }

    @Transactional
    public boolean createPost(Long memberId, PostRequest.CreatePost createPost) {
        Member member = memberRepository.safeFindById(memberId);
        Location location = member.getLocation();
        Tag tag = TagConverter.toTag(createPost);
        Post post = PostConverter.toPost(member, location, tag, createPost);
        Recommendation recommendation = RecommendationConverter.toRecommendation(member, post);

        recommendationRepository.save(recommendation);
        postRepository.save(post);
        tagRepository.save(tag);

        return true;
    }
}
