package org.pknu.weather.repository;

import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.pknu.weather.common.TestDataCreator;
import org.pknu.weather.common.TestUtil;
import org.pknu.weather.config.DataJpaTestConfig;
import org.pknu.weather.domain.Location;
import org.pknu.weather.member.entity.Member;
import org.pknu.weather.domain.Post;
import org.pknu.weather.domain.common.PostType;
import org.pknu.weather.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@DataJpaTest
@Import(DataJpaTestConfig.class)
class PostCustomRepositoryTest {
    @Autowired
    PostRepository postRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    LocationRepository locationRepository;

    @Autowired
    EntityManager em;

    @Test
    @Transactional
    @DisplayName("최신순으로 게시글을 가져오는 메서드 테스트")
    void getLatestPostListTest() {
        // given
        Location location = locationRepository.save(TestDataCreator.getBusanLocation());
        Member member = memberRepository.save(TestDataCreator.getBusanMember());

        postRepository.save(TestDataCreator.getPost(member));
        postRepository.save(TestDataCreator.getPost(member));
        postRepository.save(TestDataCreator.getPost(member));
        postRepository.save(TestDataCreator.getPost(member));
        postRepository.save(TestDataCreator.getPost(member));
        postRepository.save(TestDataCreator.getPost(member));

        em.flush();
        em.clear();

        // when
        List<Post> latestPostList = postRepository.findAllWithinDistance(1L, 4L, location, PostType.WEATHER);

        // then
        Assertions.assertThat(latestPostList.get(0).getCreatedAt()).isAfter(latestPostList.get(1).getCreatedAt());
        Assertions.assertThat(latestPostList.get(1).getCreatedAt()).isAfter(latestPostList.get(2).getCreatedAt());
        Assertions.assertThat(latestPostList.get(2).getCreatedAt()).isAfter(latestPostList.get(3).getCreatedAt());
        Assertions.assertThat(latestPostList.get(3).getCreatedAt()).isAfter(latestPostList.get(4).getCreatedAt());
        Assertions.assertThat(latestPostList.size()).isEqualTo(5);
    }

    @Test
    @Transactional
    void 게시글이_생성된_시간이_24시간이_초과했으면_조회되지_않습니다() {
        // given
        Location location = locationRepository.save(TestDataCreator.getSeoulLocation());
        Member member = memberRepository.save(TestDataCreator.getBusanMember());
        Post post = TestDataCreator.getPost(member);
        em.detach(post);

        TestUtil.entitySetFiled(post,
                "createdAt",
                LocalDateTime.now().minusHours(24));

        em.merge(post);
        em.flush();
        em.clear();

        // when
        List<Post> postList = postRepository.findAllWithinDistance(1L, 1L, location, PostType.WEATHER);

        // then
        Assertions.assertThat(postList.size()).isEqualTo(0);
    }

    @Test
    @Transactional
    void 게시글이_24시간_이내에_생성되었다면_조회됩니다() {
        // given
        Location location = locationRepository.save(TestDataCreator.getBusanLocation());
        Member member = memberRepository.save(TestDataCreator.getBusanMember());
        Post post = TestDataCreator.getPost(member);
        em.detach(post);

        TestUtil.entitySetFiled(post,
                "createdAt",
                LocalDateTime.now().minusHours(23));

        em.merge(post);
        em.flush();
        em.clear();

        // when
        List<Post> postList = postRepository.findAllWithinDistance(1L, 1L, location, PostType.WEATHER);

        // then
        Assertions.assertThat(postList.size()).isEqualTo(1);
    }
}

 