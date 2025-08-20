package org.pknu.weather.alarm.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.pknu.weather.alarm.controller.AlarmControllerV2;
import org.pknu.weather.alarm.dto.AlarmRequestDTO;
import org.pknu.weather.alarm.entity.Alarm;
import org.pknu.weather.alarm.enums.SummaryAlarmTime;
import org.pknu.weather.alarm.repository.AlarmRepository;
import org.pknu.weather.apipayload.code.status.ErrorStatus;
import org.pknu.weather.common.TestUtil;
import org.pknu.weather.exception.GeneralException;
import org.pknu.weather.location.entity.Location;
import org.pknu.weather.location.repository.LocationRepository;
import org.pknu.weather.member.entity.Member;
import org.pknu.weather.member.repository.MemberRepository;
import org.pknu.weather.security.jwt.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.MethodMode;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class AlarmCrudTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    JWTUtil jwtUtil;

    @Autowired
    AlarmControllerV2 alarmController;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private AlarmRepository alarmRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    CacheManager cm;

    @BeforeEach
    void init() {
        cm.getCacheNames()
                .forEach(name -> Objects.requireNonNull(cm.getCache(name)).clear());
    }

    private static final int DUMMY_DATA_SIZE = 3;

    List<Location> savedLocations;
    List<Member> savedMembers;
    List<Alarm> savedAlarms;


    @BeforeEach
    void setUp() {
        List<Location> transientLocations = new ArrayList<>();
        for (int i = 0; i < DUMMY_DATA_SIZE; i++) {
            Location location = Location.builder()
                    .city("city" + i)
                    .build();
            transientLocations.add(location);
        }

        savedLocations = locationRepository.saveAll(transientLocations);


        List<Member> transientMembers = new ArrayList<>();

        for (int i = 0; i < DUMMY_DATA_SIZE; i++) {
            Member member = Member.builder()
                    .email("email" + i)
                    .location(savedLocations.get(i))
                    .build();
            transientMembers.add(member);
        }

        savedMembers = memberRepository.saveAll(transientMembers);


        List<Alarm> transientAlarms = new ArrayList<>();
        for (Member member : savedMembers) {
            Alarm alarm = Alarm.builder()
                    .member(member)
                    .agreeDustAlarm(true)
                    .agreeUvAlarm(true)
                    .agreePrecipAlarm(true)
                    .agreeTempAlarm(true)
                    .agreeLiveRainAlarm(false)
                    .fcmToken("FcmToken" + (member.getId()))
                    .summaryAlarmTimes(Set.of(SummaryAlarmTime.MORNING, SummaryAlarmTime.AFTERNOON, SummaryAlarmTime.EVENING))
                    .build();
            transientAlarms.add(alarm);
        }
        savedAlarms = alarmRepository.saveAll(transientAlarms);

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    public void 알람_저장이_성공한다() throws Exception {

        Member member = saveMember();

        String authHeader = TestUtil.generateJwtToken(jwtUtil, member);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("fcmToken", "newFcmToken");

        ObjectMapper objectMapper = new ObjectMapper();

        mockMvc.perform(post("/api/v2/alarm")
                        .header("Authorization", authHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andDo(print());
    }


    @Test
    @DirtiesContext(methodMode = MethodMode.BEFORE_METHOD)
    public void 알람_저장_시_fcm토큰의_중복될_때_예외가_발생한다() throws Exception {

        Member member = saveMember();

        String authHeader = TestUtil.generateJwtToken(jwtUtil, member);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("fcmToken", "FcmToken1");

        mockMvc.perform(post("/api/v2/alarm")
                .header("Authorization", authHeader)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isConflict())
                .andDo(print());
    }


    public Member saveMember() {
        Member member = Member.builder()
                .email("email")
                .nickname("nickname")
                .build();

        Member savedMember = memberRepository.save(member);

        Alarm alarm = Alarm.builder()
                .member(member)
                .fcmToken("FcmToken1")
                .build();

        alarmRepository.save(alarm);


        entityManager.flush();
        entityManager.clear();

        return savedMember;
    }


    @Test
    void 알람_수정이_성공한다() throws Exception {
        Alarm initAlarm = savedAlarms.get(0);
        Member member = initAlarm.getMember();
        String initFcmToken = initAlarm.getFcmToken();

        String authHeader = TestUtil.generateJwtToken(jwtUtil, member);

        AlarmRequestDTO requestDto = AlarmRequestDTO.builder()
                .fcmToken(initFcmToken)
                .agreeDustAlarm(false)
                .summaryAlarmTimes(Set.of(SummaryAlarmTime.MORNING))
                .build();


        // when & then
        mockMvc.perform(patch("/api/v2/alarm")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", authHeader)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk());

        Optional<Alarm> updatedAlarmOptional = alarmRepository.findByFcmTokenAndMember(initFcmToken, member);

        assertThat(updatedAlarmOptional).isPresent();
        Alarm updatedAlarm = updatedAlarmOptional.get();

        assertThat(updatedAlarm.getAgreeDustAlarm()).isEqualTo(requestDto.getAgreeDustAlarm());
        assertThat(updatedAlarm.getSummaryAlarmTimes()).isEqualTo(requestDto.getSummaryAlarmTimes());

    }

    @Test
    void 잘못된_FcmToken을_포함한_요청은_실패한다() throws Exception {
        Member member = saveMember();

        String authHeader = TestUtil.generateJwtToken(jwtUtil, member);


        AlarmRequestDTO requestDto = new AlarmRequestDTO();
        requestDto.setFcmToken("nonExist-token");

        mockMvc.perform(patch("/api/v2/alarm")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", authHeader)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().is4xxClientError())
                .andExpect(result -> {
                    Exception resolvedException = result.getResolvedException();
                    assertThat(resolvedException).isInstanceOf(GeneralException.class);
                    assertThat(resolvedException).extracting("code").isEqualTo(ErrorStatus._FCMTOKEN_NOT_FOUND);
                });
    }
}