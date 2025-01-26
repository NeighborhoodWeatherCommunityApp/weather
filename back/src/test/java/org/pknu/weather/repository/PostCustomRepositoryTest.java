package org.pknu.weather.repository;

import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.pknu.weather.common.TestDataCreator;
import org.pknu.weather.config.TestConfig;
import org.pknu.weather.domain.Location;
import org.pknu.weather.domain.Member;
import org.pknu.weather.domain.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@DataJpaTest
@Import(TestConfig.class)
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
        Location seoulLocation = locationRepository.save(TestDataCreator.getSeoulLocation());

        Member member = memberRepository.save(TestDataCreator.getMember());
        Post testPost = TestDataCreator.getPost(member);
        for (int i = 0; i < 5; i++) {
            postRepository.save(Post.builder()
                    .content("")
                    .postType(testPost.getPostType())
                    .location(testPost.getLocation())
                    .member(member)
                    .build());
            postRepository.save(TestDataCreator.getPost(member));
        }

        em.flush();
        em.clear();

        // when
        List<Post> latestPostList = postRepository.getLatestPostList(seoulLocation);

        // then
        for (int i = 0; i < 4; i++) {
            Assertions.assertThat(latestPostList.get(i).getCreatedAt()).isAfter(latestPostList.get(i + 1).getCreatedAt());
            Assertions.assertThat(latestPostList.get(i).getContent()).isNotEmpty();
        }

        Assertions.assertThat(latestPostList.size()).isEqualTo(5);
    }
}

 