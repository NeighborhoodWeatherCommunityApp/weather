package org.pknu.weather.controller;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.pknu.weather.common.TestDataCreator;
import org.pknu.weather.common.TestUtil;
import org.pknu.weather.domain.Member;
import org.pknu.weather.domain.Post;
import org.pknu.weather.repository.MemberRepository;
import org.pknu.weather.repository.PostRepository;
import org.pknu.weather.security.util.JWTUtil;
import org.pknu.weather.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
    PostService postService;

    @Autowired
    EntityManager em;

    @Autowired
    JWTUtil jwtUtil;

    @Test
    @Transactional
    public void 좋아요_테스트() throws Exception {
        // given
        Member member1 = memberRepository.save(TestDataCreator.getBusanMember("test1"));
        String member1Token = TestUtil.generateJwtToken(jwtUtil, member1);
        Member member2 = memberRepository.save(TestDataCreator.getBusanMember("test2"));
        String member2Token = TestUtil.generateJwtToken(jwtUtil, member2);
        Post post = postRepository.save(TestDataCreator.getPost(member1));

        em.flush();
        em.clear();

        // member1이 post1에 좋아요 요청
        addRecommendation(post, member1Token);

        // 조회
        mockMvc.perform(get("/api/v1/posts/popular")
                        .header("Authorization", member1Token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.postInfo.likeClickable").value(false))
                .andExpect(jsonPath("$.result.postInfo.likeCount").value(1)) ;

        addRecommendation(post, member2Token);

        mockMvc.perform(get("/api/v1/posts/popular")
                        .header("Authorization", member2Token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.postInfo.likeClickable").value(false))
                .andExpect(jsonPath("$.result.postInfo.likeCount").value(2)) ;

        addRecommendation(post, member2Token);

        mockMvc.perform(get("/api/v1/posts/popular")
                        .header("Authorization", member2Token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.postInfo.likeClickable").value(true))
                .andExpect(jsonPath("$.result.postInfo.likeCount").value(1));
    }

    public void addRecommendation(Post post, String token) throws Exception {
        mockMvc.perform(post("/api/v1/post/recommendation")
                        .param("postId", String.valueOf(post.getId()))
                        .header("Authorization", token))
                .andExpect(status().isOk());
    }
}