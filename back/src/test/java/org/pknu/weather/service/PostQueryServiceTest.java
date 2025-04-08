package org.pknu.weather.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.pknu.weather.domain.Location;
import org.pknu.weather.domain.Member;
import org.pknu.weather.domain.Post;
import org.pknu.weather.domain.Recommendation;
import org.pknu.weather.domain.common.PostType;
import org.pknu.weather.domain.common.Sensitivity;
import org.pknu.weather.dto.PostResponse;
import org.pknu.weather.repository.LocationRepository;
import org.pknu.weather.repository.MemberRepository;
import org.pknu.weather.repository.PostRepository;
import org.pknu.weather.repository.RecommendationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class PostQueryServiceTest {
    private final double LATITUDE = 35.1845361111111;
    private final double LONGITUDE = 128.989688888888;

    @Autowired
    PostQueryService postQueryService;

    @Autowired
    PostRepository postRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    RecommendationRepository recommendationRepository;

    @Autowired
    LocationRepository locationRepository;

    Member member;

    List<Member> memberList = new ArrayList<>();
    List<Post> postList = new ArrayList<>();
    List<Recommendation> recommendationList = new ArrayList<>();

    @BeforeEach
    void init() {
        for (int i = 1; i <= 30; i++) {
            Location location = Location.builder()
                    .city("city")
                    .province("province")
                    .street("street")
                    .latitude(LATITUDE)
                    .longitude(LONGITUDE)
                    .build();

            Member m = Member.builder()
                    .location(location)
                    .email(String.valueOf(i))
                    .nickname("nickname" + i)
                    .sensitivity(Sensitivity.NONE)
                    .build();

            m = memberRepository.save(m);
            memberList.add(m);
        }

        member = memberList.get(0);

        Post post = Post.builder()
                .member(member)
                .location(member.getLocation())
                .content("s")
                .build();

        post = postRepository.save(post);
        postList.add(post);

        for (Member m : memberList) {
            Recommendation recommendation = Recommendation.builder()
                    .member(m)
                    .post(post)
                    .build();

            recommendation = recommendationRepository.save(recommendation);
            recommendationList.add(recommendation);
        }
    }

    @Test
//    @Transactional TODO: 원인 파악 이슈 #80
    void post를_조회할때_좋아요_갯수도_함께_반환() {
        // given

        // when
        PostResponse.PostList posts = postQueryService.getWeatherPosts(member.getEmail(), 1L, 5L,
                PostType.WEATHER.toString(), 0L);
        PostResponse.Post post = posts.getPostList().get(0);

        // then
        assertThat(post.getPostInfo().getLikeCount()).isEqualTo(30);
        assertThat(post.getPostInfo().getLikeClickable()).isEqualTo(false);
    }

    @AfterEach
    void tearDown() {
        recommendationRepository.deleteAll(recommendationList);
        postRepository.deleteAll(postList);
        memberRepository.deleteAll(memberList);
    }
}
