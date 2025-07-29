package org.pknu.weather.controller;

import static org.mockito.Mockito.when;

import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pknu.weather.common.TestDataCreator;
import org.pknu.weather.member.entity.Member;
import org.pknu.weather.member.repository.MemberRepository;
import org.pknu.weather.service.ExpRewardService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
public class PostControllerV1Test {
    @InjectMocks
    PostControllerV1 postControllerV1;

    @Mock
    ExpRewardService expRewardService;

    @Mock
    MemberRepository memberRepository;

    MockMvc mockMvc;
    Gson gson;

    @BeforeEach
    public void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(postControllerV1)
                .build();
    }

    //    @Test
    void 게시글작성성공_경험치증가() {
        // given
        Member member = TestDataCreator.getBusanMember();

        when(memberRepository.save(member)).thenReturn(member);

        // when

        // then
    }

    //    @Test
    void 게시글작성실패_경험치그대로() {
        // given

        // when

        // then
    }
}
