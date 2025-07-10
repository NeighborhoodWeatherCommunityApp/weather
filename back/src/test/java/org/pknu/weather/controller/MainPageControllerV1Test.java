package org.pknu.weather.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.pknu.weather.common.TestDataCreator;
import org.pknu.weather.common.TestUtil;
import org.pknu.weather.domain.Location;
import org.pknu.weather.domain.Member;
import org.pknu.weather.domain.Post;
import org.pknu.weather.repository.LocationRepository;
import org.pknu.weather.repository.MemberRepository;
import org.pknu.weather.repository.PostRepository;
import org.pknu.weather.security.util.JWTUtil;
import org.pknu.weather.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
class MainPageControllerV1Test {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    LocationRepository locationRepository;

    @Autowired
    PostService postService;

    @Autowired
    EntityManager em;

    @Autowired
    JWTUtil jwtUtil;

    @Test
    @Transactional
    public void 좋아요_테스트() throws Exception {
        // given
        Location location = locationRepository.save(TestDataCreator.getBusanLocation());
        Member member1 = memberRepository.save(TestDataCreator.getMember(1L, "test1", location));
        Member member2 = memberRepository.save(TestDataCreator.getMember(2L, "test2", location));
        String member1Token = TestUtil.generateJwtToken(jwtUtil, member1);
        String member2Token = TestUtil.generateJwtToken(jwtUtil, member2);
        Post post = postRepository.save(TestDataCreator.getPost(member1));
        flushAndClear();

        getPosts(member1Token, true, 0);

        // member1이 post1에 좋아요
        addRecommendation(post, member1Token);
        flushAndClear();
        getPosts(member1Token, false, 1);

        // member2가 post1에 좋아요
        addRecommendation(post, member2Token);
        flushAndClear();
        getPosts(member2Token, false, 2);

        // member2가 post1에 좋아요 취소
        addRecommendation(post, member2Token);
        flushAndClear();
        getPosts(member2Token, true, 1);
    }

    private void flushAndClear() {
        em.flush();
        em.clear();
    }

    private void addRecommendation(Post post, String token) throws Exception {
        mockMvc.perform(post("/api/v1/post/recommendation")
                        .param("postId", String.valueOf(post.getId()))
                        .header("Authorization", token))
                .andExpect(status().isOk());
    }

    private void getPosts(String token, boolean likeClickableExpect, int likeCountExpect) throws Exception {
        mockMvc.perform(get("/api/v1/main/posts/popular")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result[0].postInfo.likeClickable").value(likeClickableExpect))
                .andExpect(jsonPath("$.result[0].postInfo.likeCount").value(likeCountExpect));
    }
}