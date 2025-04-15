package org.pknu.weather.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pknu.weather.common.mapper.EnumTagMapper;
import org.pknu.weather.domain.Location;
import org.pknu.weather.domain.Member;
import org.pknu.weather.domain.Post;
import org.pknu.weather.domain.Tag;
import org.pknu.weather.domain.common.PostType;
import org.pknu.weather.domain.exp.ExpEvent;
import org.pknu.weather.dto.PostRequest;
import org.pknu.weather.dto.converter.PostConverter;
import org.pknu.weather.dto.converter.TagConverter;
import org.pknu.weather.repository.LocationRepository;
import org.pknu.weather.repository.MemberRepository;
import org.pknu.weather.repository.PostRepository;
import org.pknu.weather.repository.RecommendationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final RecommendationRepository recommendationRepository;
    private final LocationRepository locationRepository;
    private final EnumTagMapper enumTagMapper;
    private final ExpRewardService expRewardService;

    @Transactional(readOnly = true)
    public List<Post> getPosts(Long memberId, Long lastPostId, Long size, String postType, Long locationId) {
        Member member = memberRepository.safeFindById(memberId);
        Location location = null;

        if (locationId == 0) {
            location = member.getLocation();
        } else {
            location = locationRepository.safeFindById(locationId);
        }

        List<Post> postList = postRepository.findAllWithinDistance(lastPostId, size, location,
                PostType.toPostType(postType));

        return postList;
    }

    /**
     * 게시글과 날씨 태그를 생성합니다.
     *
     * @param email
     * @param createPost
     * @return
     */
    @Transactional
    public boolean createWeatherPost(String email, PostRequest.CreatePost createPost) {
        Member member = memberRepository.safeFindByEmail(email);
        Location location = member.getLocation();
        Tag tag = TagConverter.toTag(createPost, location);
        Post post = PostConverter.toPost(member, tag, createPost.getContent());

        post.addTag(tag);
        postRepository.save(post);

        // TODO: 이벤트 방식으로 변경
        expRewardService.rewardExp(member.getEmail(), ExpEvent.CREATE_POST);

        return true;
    }


    @Transactional
    public boolean createWeatherPostV2(String email, PostRequest.CreatePostAndTagParameters params) {
        Member member = memberRepository.safeFindByEmail(email);
        Location location = member.getLocation();

        if (!params.parametersIsEmpty()) {
            Post post = PostConverter.toPost(member, params.getContent());
            Tag tag = TagConverter.toTag(params, location, enumTagMapper);
            post.addTag(tag);
            postRepository.save(post);
            return true;
        }

        if (params.contentIsEmpty()) {
            Post post = PostConverter.toContentEmptyPost(member);
            Tag tag = TagConverter.toTag(params, location, enumTagMapper);
            post.addTag(tag);
            postRepository.save(post);
            return true;
        }

        if (params.tagKeyStringIsEmpty()) {
            Post post = PostConverter.toPost(member, params.getContent());
            postRepository.save(post);
            return true;
        }

        return true;
    }

    @Transactional
    public boolean createHobbyPost(String email, PostRequest.HobbyParams params) {
        Member member = memberRepository.safeFindByEmail(email);
        Location location = locationRepository.safeFindById(params.getLocationId());
        Post post = PostConverter.toPost(member, params);
        postRepository.save(post);
        return true;
    }
}
