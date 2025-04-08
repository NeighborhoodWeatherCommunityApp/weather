package org.pknu.weather.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
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
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
class CommunityControllerV1Test {
    @Autowired
    PostRepository postRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    PostService postService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    JWTUtil jwtUtil;

    @Test
    @Transactional
    void 게시글조회성공_작성자정보와레벨함꼐반환() throws Exception {
        // given
        Member member = memberRepository.save(TestDataCreator.getBusanMember("test1"));
        Member member2 = memberRepository.save(TestDataCreator.getBusanMember("test2"));

        Post post = TestDataCreator.getPost(member);
        Post post2 = TestDataCreator.getPost(member2);

        List<Post> postList = new ArrayList<>();
        postList.add(post);
        postList.add(post2);

        String jwt = TestUtil.generateJwtToken(jwtUtil, member);
        postRepository.saveAll(postList);

        // when
        ResultActions result = mockMvc.perform(get("/api/v1/community/posts")
                .header("Authorization", jwt));

        // then
        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.postList[0].memberInfo.memberName").value(member2.getNickname()))
                .andExpect(jsonPath("$.result.postList[0].memberInfo.levelName").value(member2.getLevel().name()))
                .andExpect(jsonPath("$.result.postList[1].memberInfo.memberName").value(member.getNickname()))
                .andExpect(jsonPath("$.result.postList[1].memberInfo.levelName").value(member.getLevel().name()));
    }
}