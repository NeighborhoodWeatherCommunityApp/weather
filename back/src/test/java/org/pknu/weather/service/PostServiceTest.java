package org.pknu.weather.service;
/*
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.pknu.weather.domain.Location;
import org.pknu.weather.domain.Member;
import org.pknu.weather.domain.Post;
import org.pknu.weather.domain.common.Sensitivity;
import org.pknu.weather.domain.tag.*;
import org.pknu.weather.dto.PostRequest;
import org.pknu.weather.dto.converter.PostRequestConverter;
import org.pknu.weather.repository.LocationRepository;
import org.pknu.weather.repository.MemberRepository;
import org.pknu.weather.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Slf4j
class PostServiceTest {

    @Autowired
    PostService postService;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    LocationRepository locationRepository;

    @Autowired
    PostRequestConverter postRequestConverter;

    private final double LATITUDE = 35.1845361111111;
    private final double LONGITUDE = 128.989688888888;

    @BeforeEach
    void init() {
        Location location = Location.builder()
                .city("city")
                .province("province")
                .street("street")
                .latitude(LATITUDE)
                .longitude(LONGITUDE)
                .build();

        Member member = Member.builder()
                .location(location)
                .email("email@naver.com")
                .nickname("nickname")
                .sensitivity(Sensitivity.NONE)
                .build();

        memberRepository.save(member);
    }

    @Test
    @DisplayName("post 저장 테스트")
    void postSaveTest() {
        // given
        PostRequest.CreatePost createPost = PostRequest.CreatePost.builder()
                .content("test")
                .temperatureTag(TemperatureTag.HOT)
                .humidityTag(HumidityTag.HUMID)
                .skyTag(SkyTag.RAIN)
                .windTag(WindTag.NONE)
                .dustTag(DustTag.GOOD)
                .build();

        Member member = memberRepository.findAll().stream()
                .filter(m -> m.getNickname().equals("nickname"))
                .findFirst()
                .get();

        // when
        postService.createPost(member.getId(), createPost);
        Post post = postRepository.findAll().get(0);

        // then
        assertThat(post.getContent()).isEqualTo("test");
        assertThat(post.getMember().getId()).isEqualTo(member.getId());
    }
}

 */