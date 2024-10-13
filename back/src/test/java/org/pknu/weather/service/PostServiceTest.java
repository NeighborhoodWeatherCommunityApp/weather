package org.pknu.weather.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.pknu.weather.common.TestDataCreator;
import org.pknu.weather.common.utils.GeometryUtils;
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
import org.pknu.weather.repository.RecommendationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

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
    RecommendationRepository recommendationRepository;

    @Autowired
    PostRequestConverter postRequestConverter;


//    @BeforeEach
//    void init() {
//        String[][] list = {
//                {"서울특별시", "종로구", "청운효자동"},
//                {"서울특별시", "종로구", "사직동"},
//                {"서울특별시", "종로구", "삼청동"},
//                {"서울특별시", "종로구", "부암동"},
//                {"부산광역시", "부산진구", "부전제2동"},
//                {"부산광역시", "부산진구", "연지동"},
//                {"부산광역시", "부산진구", "초읍동"},
//                {"부산광역시", "부산진구", "양정제1동"}
//        };
//
//        double[][] latlngList = {
//                {126.9706519, 37.5841367},
//                {126.970955555555, 37.5732694444444},
//                {126.983977777777, 37.582425},
//                {126.966444444444, 37.5898555555555},
//                {129.059075, 35.1495222222222},
//                {129.055008333333, 35.1697138888888},
//                {129.049833333333, 35.175625},
//                {129.066655555555, 35.1713972222222}
//        };
//
//        for (int i = 0; i < list.length; i++) {
//            double latitude = latlngList[i][1];
//            double longitude = latlngList[i][0];
//
//            Location location = Location.builder()
//                    .point(GeometryUtils.getPoint(latitude, longitude))
//                    .province(list[i][0])
//                    .city(list[i][1])
//                    .street(list[i][2])
//                    .latitude(latitude)
//                    .longitude(longitude)
//                    .build();
//
//            Member member = Member.builder()
//                    .location(location)
//                    .email(i + "email@naver.com")
//                    .nickname(i + "user")
//                    .sensitivity(Sensitivity.NONE)
//                    .build();
//
//            memberRepository.save(member);
//        }
//    }

    @Test
    @DisplayName("post 저장 테스트")
    @Transactional
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

        Member member = memberRepository.save(Member.builder()
                .location(TestDataCreator.getBusanLocation())
                .nickname("member")
                .build());

        // when
        boolean result = postService.createPost(member.getId(), createPost);

        // then
        Post post = postRepository.findAll().get(0);
        assertThat(post.getContent()).isEqualTo("test");
        assertThat(post.getMember().getId()).isEqualTo(member.getId());
    }

    @Test
    @Transactional
    void 같은_동네의_글만_조회하는_테스트() {
        // given
        Member busanMember = memberRepository.save(Member.builder()
                .location(TestDataCreator.getBusanLocation())
                .nickname("busan member")
                .build());

        Member seoulMember = memberRepository.save(Member.builder()
                .location(TestDataCreator.getSeoulLocation())
                .nickname("seoul member")
                .build());

        Post busanPost = postRepository.save(Post.builder()
                .member(busanMember)
                .location(busanMember.getLocation())
                .build());

        Post seoulPost = postRepository.save(Post.builder()
                .member(seoulMember)
                .location(seoulMember.getLocation())
                .build());

        // when
        List<Post> postList = postRepository.findAllWithinDistance(1L, 5L, busanMember.getLocation());

        // then
        assertThat(postList.size()).isEqualTo(1);
    }
}
