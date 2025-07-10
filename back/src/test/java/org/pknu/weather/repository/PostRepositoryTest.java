package org.pknu.weather.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.pknu.weather.common.TestDataCreator;
import org.pknu.weather.config.DataJpaTestConfig;
import org.pknu.weather.domain.Location;
import org.pknu.weather.domain.Member;
import org.pknu.weather.domain.Post;
import org.pknu.weather.domain.Recommendation;
import org.pknu.weather.domain.common.Sensitivity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

@DataJpaTest
@Import(DataJpaTestConfig.class)
class PostRepositoryTest {

    @Autowired
    PostRepository postRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    LocationRepository locationRepository;

    @Autowired
    RecommendationRepository recommendationRepository;

    private final double LATITUDE = 35.1845361111111;
    private final double LONGITUDE = 128.989688888888;

    void init() {
        List<Member> memberList = new ArrayList<>();
        List<Post> postList = new ArrayList<>();

        for (int i = 1; i <= 5; i++) {
            Location location = Location.builder()
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
        init();
        List<Recommendation> all = recommendationRepository.findAll();
        // when
        Location location = locationRepository.findAll().get(0);
        List<Post> popularPostList = postRepository.getPopularPostList(location);

        // then
        assertThat(popularPostList.get(0).getContent()).isEqualTo("content5");
        assertThat(popularPostList.get(1).getContent()).isEqualTo("content4");
        assertThat(popularPostList.get(2).getContent()).isEqualTo("content3");
        assertThat(popularPostList.get(3).getContent()).isEqualTo("content2");
        assertThat(popularPostList.get(4).getContent()).isEqualTo("content1");
    }

    @Test
    void 하루에_게시글_몇개_작성했는지_반환하는_메서드_테스트() {
        // given
        Member member = memberRepository.save(TestDataCreator.getBusanMember());
        postRepository.save(TestDataCreator.getPost(member));
        postRepository.save(TestDataCreator.getPost(member));
        postRepository.save(TestDataCreator.getPost(member));

        // when
        Integer countTodayPost = postRepository.countTodayPostByMemberId(member.getId());

        // then
        assertThat(countTodayPost).isEqualTo(3);
    }
}
