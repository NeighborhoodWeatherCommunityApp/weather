package org.pknu.weather.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.pknu.weather.common.TestDataCreator;
import org.pknu.weather.common.TestUtil;
import org.pknu.weather.domain.Member;
import org.pknu.weather.domain.MemberTerms;
import org.pknu.weather.domain.Terms;
import org.pknu.weather.domain.common.TermsType;
import org.pknu.weather.domain.exp.Level;
import org.pknu.weather.dto.MemberJoinDTO;
import org.pknu.weather.dto.TermsDto;
import org.pknu.weather.repository.MemberRepository;
import org.pknu.weather.repository.MemberTermsRepository;
import org.pknu.weather.repository.TermsRepository;
import org.pknu.weather.security.util.JWTUtil;
import org.pknu.weather.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
class MemberControllerV1Test {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    TermsRepository termsRepository;

    @Autowired
    MemberTermsRepository memberTermsRepository;

    @Autowired
    MemberService memberService;

    @Autowired
    EntityManager em;

    @Autowired
    JWTUtil jwtUtil;

    @Autowired
    ObjectMapper objectMapper;

    //    @Test
//    @Transactional
    void saveMemberInfo() throws Exception {
        // given
        init();

        em.flush();
        em.clear();

        MockMultipartFile profileImgFile = getProfileImage();
        TermsDto termsDtoAgreed = getTermsDto(true);
        Member member = memberRepository.findMemberByEmail(TestDataCreator.getBusanMember().getEmail()).get();
        MemberJoinDTO memberJoinDTO = getMemberJoinDTO(member);
        String jwt = generateJwtToken(member.getId(), member.getEmail());

        // mockMvc를 사용하여 실제 컨트롤러 호출
        mockMvc.perform(multipart("/api/v1/member/info")
                        .file(profileImgFile)  // 파일 업로드
                        .param("sensitivity", memberJoinDTO.getSensitivity().toString())  // MemberJoinDTO 데이터
                        .param("nickname", memberJoinDTO.getNickname())  // MemberJoinDTO 데이터
                        .param("isServiceTermsAgreed", termsDtoAgreed.getIsServiceTermsAgreed().toString())    // TermsDto 데이터
                        .param("isPrivacyPolicyAgreed", termsDtoAgreed.getIsPrivacyPolicyAgreed().toString())    // TermsDto 데이터
                        .param("isLocationServiceTermsAgreed",
                                termsDtoAgreed.getIsLocationServiceTermsAgreed().toString())    // TermsDto 데이터
                        .param("isPushNotificationAgreed",
                                termsDtoAgreed.getIsPushNotificationAgreed().toString())    // TermsDto 데이터
                        .header("Authorization", jwt))  // Authorization 헤더
                .andExpect(status().isOk())  // 응답 상태 코드 200 OK 확인
                .andExpect(jsonPath("$.result.nickname").value(member.getNickname()))  // 응답 바디 검증
                .andExpect(jsonPath("$.result.email").value(member.getEmail()));  // 응답 바디 검증

        List<MemberTerms> terms = memberTermsRepository.findAll();
        Assertions.assertThat(terms.size()).isEqualTo(4);
    }

    @Test
    @Transactional
    void setTermsTest() throws Exception {
        String email = "setTermsTest@naver.com";
        init();
        memberRepository.save(Member.builder()
                .email(email)
                .build());
        em.flush();
        em.clear();

        TermsDto termsDtoAgreed = getTermsDto(true);
        Member member = memberRepository.findMemberByEmail(email).get();
        String jwt = generateJwtToken(member.getId(), member.getEmail());

        mockMvc.perform(post("/api/v1/member/terms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(termsDtoAgreed))
                        .header("Authorization", jwt))
                .andExpect(status().isOk()); // 응답 상태 코드 200 OK 확인

        List<MemberTerms> terms = memberTermsRepository.findAll();

        Assertions.assertThat(terms.size()).isEqualTo(4);
    }

    MockMultipartFile getProfileImage() throws IOException {
        BufferedImage dummyImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(dummyImage, "jpeg", outputStream);
        InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

        // MockMultipartFile 생성
        return new MockMultipartFile(
                "profileImg",
                "test.jpg",
                "image/jpeg",
                inputStream
        );
    }

    MemberJoinDTO getMemberJoinDTO(Member member) {
        return MemberJoinDTO.builder()
                .nickname(member.getNickname())
                .profileImg(new MockMultipartFile("file", "test.jpg", "image/jpeg", "This is a test image.".getBytes()))
                .sensitivity(member.getSensitivity())
                .build();
    }

    TermsDto getTermsDto(Boolean isRequired) {
        return TermsDto.builder()
                .isLocationServiceTermsAgreed(isRequired)
                .isServiceTermsAgreed(isRequired)
                .isPrivacyPolicyAgreed(isRequired)
                .isPushNotificationAgreed(isRequired)
                .build();
    }

    @Transactional
    void init() {
        termsRepository.save(Terms.builder()
                .isRequired(true)
                .termsType(TermsType.SERVICE_TERMS)
                .termsVersion(1)
                .build());

        termsRepository.save(Terms.builder()
                .isRequired(true)
                .termsType(TermsType.LOCATION_SERVICE_TERMS)
                .termsVersion(1)
                .build());

        termsRepository.save(Terms.builder()
                .isRequired(true)
                .termsType(TermsType.PRIVACY_POLICY)
                .termsVersion(1)
                .build());

        termsRepository.save(Terms.builder()
                .isRequired(false)
                .termsType(TermsType.PUSH_NOTIFICATION)
                .termsVersion(1)
                .build());
    }

    private String generateJwtToken(Long id, String email) {
        // JWT 토큰을 생성하는 메서드
        Map<String, Object> claims = Map.of("id", id, "email", email);
        return "Bearer " + jwtUtil.generateToken(claims, 1);
    }

    @Test
    void 사용자정보조회성공() throws Exception {
        // given
        Member member = memberRepository.save(TestDataCreator.getBusanMember());
        String jwt = TestUtil.generateJwtToken(jwtUtil, member);

        // when
        ResultActions result = mockMvc.perform(get("/api/v1/member/info")
                .header("Authorization", jwt));

        // then
        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.email").value(member.getEmail()))
                .andExpect(jsonPath("$.result.nickname").value(member.getNickname()))
                .andExpect(jsonPath("$.result.levelKey").value(member.getLevel().name()))
                .andExpect(jsonPath("$.result.rankName").value(member.getLevel().getRankName()))
                .andExpect(jsonPath("$.result.exp").value(member.getExp()))
                .andExpect(jsonPath("$.result.nextLevelRequiredExp").value(
                        Level.getNextLevel(member.getLevel()).getRequiredExp()));
    }
}
