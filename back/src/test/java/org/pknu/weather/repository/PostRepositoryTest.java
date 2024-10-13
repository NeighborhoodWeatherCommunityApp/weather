package org.pknu.weather.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.pknu.weather.common.utils.GeometryUtils;
import org.pknu.weather.domain.Location;
import org.pknu.weather.domain.Member;
import org.pknu.weather.domain.Post;
import org.pknu.weather.domain.Recommendation;
import org.pknu.weather.domain.common.Sensitivity;
import org.pknu.weather.service.PostQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class PostRepositoryTest {

    @Autowired
    PostRepository postRepository;

    @Autowired
    PostQueryService postQueryService;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    LocationRepository locationRepository;

    @Autowired
    RecommendationRepository recommendationRepository;

    private final double LATITUDE = 35.1845361111111;
    private final double LONGITUDE = 128.989688888888;

    @BeforeEach
    void init() {
        List<Member> memberList = new ArrayList<>();
        List<Post> postList = new ArrayList<>();

        for (int i = 1; i <= 5; i++) {
            Location location = Location.builder()
                    .point(GeometryUtils.getPoint(LATITUDE, LONGITUDE))
                    .city("city" + i)
                    .province("province" + i)
                    .street("street" + i)
                    .latitude(LATITUDE)
                    .longitude(LONGITUDE)
                    .build();

            location = locationRepository.save(location);

            Member member = Member.builder()
                    .location(location)
                    .email("test" + i)
                    .nickname("nickname" + i)
                    .sensitivity(Sensitivity.NONE)
                    .build();

            memberList.add(member);
            member = memberRepository.save(member);

            Post post = Post.builder()
                    .member(member)
                    .location(member.getLocation())
                    .content("content" + i)
                    .build();

            post = postRepository.save(post);
            postList.add(post);
        }

        for (int i = 5; i >= 1; i--) {
            for (int j = 1; j <= i; j++) {
                Recommendation recommendation = Recommendation.builder()
                        .member(memberList.get(j - 1))
                        .post(postList.get(i - 1))
                        .build();

                recommendationRepository.save(recommendation);
            }
        }
    }

    @Test
    @Transactional
    void 지역에서_좋아요를_많이_받은_게시글_5개_불러오기() {
        // given
        List<Recommendation> all = recommendationRepository.findAll();
        // when
        Location location = locationRepository.findAll().get(0);
        List<Post> popularPostList = postRepository.getPopularPostList(location);

        // then
        Assertions.assertThat(popularPostList.get(0).getContent()).isEqualTo("content5");
        Assertions.assertThat(popularPostList.get(1).getContent()).isEqualTo("content4");
        Assertions.assertThat(popularPostList.get(2).getContent()).isEqualTo("content3");
        Assertions.assertThat(popularPostList.get(3).getContent()).isEqualTo("content2");
        Assertions.assertThat(popularPostList.get(4).getContent()).isEqualTo("content1");
    }
}
