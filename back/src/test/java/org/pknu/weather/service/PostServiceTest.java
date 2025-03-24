package org.pknu.weather.service;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.pknu.weather.common.TestDataCreator;
import org.pknu.weather.domain.Location;
import org.pknu.weather.domain.Member;
import org.pknu.weather.domain.Post;
import org.pknu.weather.domain.Weather;
import org.pknu.weather.domain.common.PostType;
import org.pknu.weather.domain.tag.DustTag;
import org.pknu.weather.domain.tag.HumidityTag;
import org.pknu.weather.domain.tag.SkyTag;
import org.pknu.weather.domain.tag.TemperatureTag;
import org.pknu.weather.domain.tag.WindTag;
import org.pknu.weather.dto.PostRequest;
import org.pknu.weather.dto.PostRequest.HobbyParams;
import org.pknu.weather.repository.LocationRepository;
import org.pknu.weather.repository.MemberRepository;
import org.pknu.weather.repository.PostRepository;
import org.pknu.weather.repository.RecommendationRepository;
import org.pknu.weather.repository.WeatherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

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
    WeatherRepository weatherRepository;

    @Autowired
    EntityManager em;

    @Test
    @Transactional
    public void 취미_글_저장_테스트() {
        // given
        Member member = memberRepository.save(Member.builder()
                .location(TestDataCreator.getBusanLocation())
                .email("test@naver.com")
                .nickname("member")
                .build());

        HobbyParams hobbyParams = HobbyParams.builder()
                .postType("PET")
                .locationId(member.getLocation().getId())
                .content("test")
                .build();

        // when
        boolean hobbyPost = postService.createHobbyPost(member.getEmail(), hobbyParams);
        Post post = postRepository.findAll().get(0);

        // then
        assertThat(hobbyPost).isTrue();
        assertThat(post.getMember().getId()).isEqualTo(member.getId());
    }

    @Test
    @DisplayName("post 저장 테스트")
    @Transactional
    void postSaveTest() {
        // given
        Location location = TestDataCreator.getBusanLocation();
        LocalDateTime now = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);

        Weather weather = Weather.builder()
                .location(location)
                .presentationTime(now)
                .basetime(now)
                .temperature(14)
                .humidity(50)
                .windSpeed(1.3)
                .build();

        weatherRepository.save(weather);

        PostRequest.CreatePost createPost = PostRequest.CreatePost.builder()
                .content("test")
                .temperatureTag(TemperatureTag.HOT)
                .humidityTag(HumidityTag.HUMID)
                .skyTag(SkyTag.RAIN)
                .windTag(WindTag.NONE)
                .dustTag(DustTag.GOOD)
                .build();

        Member member = memberRepository.save(Member.builder()
                .location(location)
                .nickname("member")
                .email("test@naver.com")
                .build());

        // when
        boolean result = postService.createWeatherPost(member.getEmail(), createPost);

        // then
        Post post = postRepository.findAll().get(0);
        assertThat(post.getContent()).isEqualTo("test");
        assertThat(post.getMember().getId()).isEqualTo(member.getId());
    }

    @ParameterizedTest
    @CsvSource({
            "content, HOT, HUMID, RAIN, GOOD, NONE",
            " , HOT, HUMID, RAIN, GOOD, NONE",
            "content, , , , , ,"
    })
    @Transactional
    void post_저장_테스트_v2(String content, String tempKey, String humidKey, String skyKey, String dustKey,
                        String windKey) {
        // given
        Location location = TestDataCreator.getBusanLocation();
        LocalDateTime now = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);

        Weather weather = Weather.builder()
                .location(location)
                .presentationTime(now)
                .basetime(now)
                .temperature(14)
                .humidity(50)
                .windSpeed(1.3)
                .build();

        weatherRepository.save(weather);

        PostRequest.CreatePostAndTagParameters createPost = PostRequest.CreatePostAndTagParameters.builder()
                .content(content)
                .temperatureTagKey(tempKey)
                .humidityTagKey(humidKey)
                .skyTagKey(skyKey)
                .windTagKey(windKey)
                .dustTagKey(dustKey)
                .build();

        Member member = memberRepository.save(Member.builder()
                .location(location)
                .nickname("member")
                .email("test@naver.com")
                .build());

        // when
        boolean result = postService.createWeatherPostV2(member.getEmail(), createPost);

        // then
        Post post = postRepository.findAll().get(0);
        assertThat(post.getContent()).isEqualTo(content);
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
                .content("test")
                .build());

        Post seoulPost = postRepository.save(Post.builder()
                .member(seoulMember)
                .location(seoulMember.getLocation())
                .content("test")
                .build());

        // when
        List<Post> postList = postRepository.findAllWithinDistance(1L, 5L, busanMember.getLocation(),
                PostType.WEATHER);

        // then
        assertThat(postList.get(0)).isEqualTo(busanPost);
    }

    //    @Test
    @Transactional
    void postType이_다르면_게시글이_조회되지_않습니다() {
        // given
        Member member = memberRepository.save(TestDataCreator.getBusanMember());

        Post hPost = postRepository.save(Post.builder()
                .postType(PostType.HIKING)
                .location(member.getLocation())
                .content("test")
                .member(member)
                .build());

        Post wPost = postRepository.save(Post.builder()
                .postType(PostType.WEATHER)
                .location(member.getLocation())
                .member(member)
                .content("test")
                .build());

        // when
        List<Post> postList = postService.getPosts(member.getId(), 1L, 5L, PostType.HIKING.toString(),
                member.getLocation().getId());

        // then
        assertThat(postList.get(0)).isEqualTo(hPost);
    }

    @Test
    @Transactional
    public void 좋아요_누르기_좋아요_취소_테스트() {
        // given
        Member member1 = memberRepository.save(TestDataCreator.getBusanMember("test1"));
        Member member2 = memberRepository.save(TestDataCreator.getBusanMember("test2"));
        Post post = postRepository.save(TestDataCreator.getPost(member1));

        // when
        postService.addRecommendation(member1.getEmail(), post.getId());
        postService.addRecommendation(member2.getEmail(), post.getId());
        em.flush();
        em.clear();

        post = postRepository.safeFindById(post.getId());
        assertThat(post.getRecommendationList().size()).isEqualTo(2);

        postService.addRecommendation(member1.getEmail(), post.getId());
        em.flush();
        em.clear();

        post = postRepository.safeFindById(post.getId());
        assertThat(post.getRecommendationList().size()).isEqualTo(1);
    }
}
