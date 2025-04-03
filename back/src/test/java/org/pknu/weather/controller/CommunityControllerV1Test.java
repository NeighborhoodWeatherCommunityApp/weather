package org.pknu.weather.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.pknu.weather.common.TestDataCreator;
import org.pknu.weather.common.TestUtil;
import org.pknu.weather.common.mapper.EnumTagMapper;
import org.pknu.weather.domain.Location;
import org.pknu.weather.domain.Member;
import org.pknu.weather.domain.Post;
import org.pknu.weather.domain.common.PostType;
import org.pknu.weather.repository.LocationRepository;
import org.pknu.weather.repository.MemberRepository;
import org.pknu.weather.repository.PostRepository;
import org.pknu.weather.repository.RecommendationRepository;
import org.pknu.weather.security.util.JWTUtil;
import org.pknu.weather.service.PostQueryService;
import org.pknu.weather.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(CommunityControllerV1.class)
class CommunityControllerV1Test {
    @MockBean
    PostRepository postRepository;

    @MockBean
    LocationRepository locationRepository;

    @MockBean
    MemberRepository memberRepository;

    @MockBean
    RecommendationRepository recommendationRepository;

    @MockBean
    EnumTagMapper enumTagMapper;

    PostQueryService postQueryService;

    @InjectMocks
    PostService postService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    JWTUtil jwtUtil;

    @BeforeEach
    void setUp() {
        postQueryService = Mockito.spy(new PostQueryService(postRepository, memberRepository, postService));
    }

    @Test
    void 게시글조회성공_작성자정보와레벨함꼐반환() throws Exception {
        // given
        Member member = Member.builder()
                .id(1L)
                .location(TestDataCreator.getBusanLocation())
                .email("test@naver.com")
                .build();

        Member member2 = Member.builder()
                .id(2L)
                .location(TestDataCreator.getBusanLocation())
                .email("test2@naver.com")
                .build();

        Post post = TestDataCreator.getPost(member);
        Post post2 = TestDataCreator.getPost(member2);

        List<Post> postList = new ArrayList<>();
        postList.add(post);
        postList.add(post2);

        String jwt = TestUtil.generateJwtToken(jwtUtil, member);

        when(memberRepository.findByEmail(any(String.class))).thenReturn(Optional.of(member));
        when(memberRepository.safeFindByEmail(any(String.class))).thenReturn(member);
        when(memberRepository.safeFindById(any(Long.class))).thenReturn(member);
        when(locationRepository.safeFindById(any(Long.class))).thenReturn(member.getLocation());
        when(postRepository.findAllWithinDistance(any(Long.class), any(Long.class), any(Location.class),
                any(PostType.class))).thenReturn(postList);

        // when
        ResultActions result = mockMvc.perform(get("/api/v1/community/posts")
                .header("Authorization", jwt));

        // then
        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.postList[0].memberInfo.memberName").value(member.getNickname()))
                .andExpect(jsonPath("$.result.postList[0].memberInfo.levelName").value(member.getLevel().name()))
                .andExpect(jsonPath("$.result.postList[1].memberInfo.memberName").value(member.getNickname()))
                .andExpect(jsonPath("$.result.postList[1].memberInfo.levelName").value(member.getLevel().name()));

        verify(postService).getPosts(any(Long.class), any(Long.class), any(Long.class), any(String.class),
                any(Long.class));
    }
}