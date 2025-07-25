package org.pknu.weather.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pknu.weather.common.mapper.EnumTagMapper;
import org.pknu.weather.domain.Location;
import org.pknu.weather.member.entity.Member;
import org.pknu.weather.domain.Post;
import org.pknu.weather.domain.Tag;
import org.pknu.weather.domain.common.PostType;
import org.pknu.weather.domain.tag.SkyTag;
import org.pknu.weather.dto.PostRequest;
import org.pknu.weather.dto.converter.PostConverter;
import org.pknu.weather.dto.converter.TagConverter;
import org.pknu.weather.event.exp.PostCreatedEvent;
import org.pknu.weather.event.alarm.LiveRainAlarmCreatedEvent;
import org.pknu.weather.repository.LocationRepository;
import org.pknu.weather.member.repository.MemberRepository;
import org.pknu.weather.repository.PostRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final LocationRepository locationRepository;
    private final EnumTagMapper enumTagMapper;
    private final ApplicationEventPublisher eventPublisher;

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
        Post savedPost = postRepository.save(post);
        eventPublisher.publishEvent(new PostCreatedEvent(member.getEmail()));

        if(tag.getSkyTag().equals(SkyTag.RAIN))
            eventPublisher.publishEvent(new LiveRainAlarmCreatedEvent(savedPost.getId()));

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
