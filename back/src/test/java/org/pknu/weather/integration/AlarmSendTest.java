package org.pknu.weather.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.internal.verification.VerificationModeFactory.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.pknu.weather.common.TestUtil;
import org.pknu.weather.controller.AlarmControllerV2;
import org.pknu.weather.domain.Alarm;
import org.pknu.weather.domain.ExtraWeather;
import org.pknu.weather.domain.Location;
import org.pknu.weather.member.entity.Member;
import org.pknu.weather.domain.Weather;
import org.pknu.weather.domain.common.RainType;
import org.pknu.weather.domain.common.SummaryAlarmTime;
import org.pknu.weather.repository.AlarmRepository;
import org.pknu.weather.repository.ExtraWeatherRepository;
import org.pknu.weather.repository.LocationRepository;
import org.pknu.weather.member.repository.MemberRepository;
import org.pknu.weather.repository.WeatherRepository;
import org.pknu.weather.security.jwt.JWTUtil;
import org.pknu.weather.service.AlarmService;
import org.pknu.weather.service.dto.AlarmInfo;
import org.pknu.weather.service.dto.WeatherSummaryAlarmInfo;
import org.pknu.weather.service.message.WeatherSummaryMessageMaker;
import org.pknu.weather.service.sender.FcmMessage;
import org.pknu.weather.service.sender.NotificationMessage;
import org.pknu.weather.service.sender.NotificationSender;
import org.pknu.weather.domain.common.AlarmType;
import org.pknu.weather.service.WeatherRefresherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.MethodMode;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class AlarmSendTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    JWTUtil jwtUtil;

    @Autowired
    AlarmControllerV2 alarmController;

    @Autowired
    private AlarmService alarmService;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private AlarmRepository alarmRepository;

    @Autowired
    private WeatherRepository weatherRepository;

    @Autowired
    private ExtraWeatherRepository extraWeatherRepository;

    @SpyBean
    private WeatherSummaryMessageMaker weatherSummaryMessageMaker;

    @SpyBean
    private NotificationSender sender;

    @MockBean
    private WeatherRefresherService weatherRefresherService;

    @Autowired
    private EntityManager entityManager;

    private static final int DUMMY_DATA_SIZE = 3;


    @Captor
    ArgumentCaptor<AlarmInfo> alarmInfoArgumentCaptor;
    @Captor
    ArgumentCaptor<NotificationMessage> notificationMessageArgumentCaptor;

    List<Location> savedLocations;
    List<Member> savedMembers;
    List<Alarm> savedAlarms;

    private static MockedStatic<LocalDateTime> mockedLocalDateTimeStatic;

    private static final LocalDateTime FIXED_CURRENT_DATETIME = LocalDateTime.of(2025, 5, 16, 13, 59, 18);


    @BeforeEach
    void beforeAll() {
        mockedLocalDateTimeStatic = Mockito.mockStatic(LocalDateTime.class, Mockito.CALLS_REAL_METHODS);
        mockedLocalDateTimeStatic.when(LocalDateTime::now).thenReturn(FIXED_CURRENT_DATETIME);
    }

    @AfterEach
    void afterEach() {
        if (mockedLocalDateTimeStatic != null) {
            mockedLocalDateTimeStatic.close();
        }
    }


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

        List<Weather> transientWeathers = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        System.out.println("-------------------------");
        System.out.println(now);
        for (Location managedLocation : savedLocations) {
            for (int j = 0; j < DUMMY_DATA_SIZE; j++) {
                Weather weather = Weather.builder()
                        .location(managedLocation)
                        .presentationTime(now.plusHours(j))
                        .temperature(15 + j)
                        .rainType(RainType.RAIN)
                        .humidity(50)
                        .windSpeed(12.0)
                        .build();
                transientWeathers.add(weather);
            }
        }
        weatherRepository.saveAll(transientWeathers);


        List<ExtraWeather> transientExtraWeathers = new ArrayList<>();
        for (Location managedLocation : savedLocations) {
            ExtraWeather extraWeather = ExtraWeather.builder()
                    .basetime(now)
                    .location(managedLocation)
                    .pm10(2)
                    .pm25(2)
                    .uv(1)
                    .uvPlus3(2)
                    .uvPlus6(3)
                    .uvPlus9(3)
                    .uvPlus12(2)
                    .build();
            transientExtraWeathers.add(extraWeather);
        }
        extraWeatherRepository.saveAll(transientExtraWeathers);

        entityManager.flush();
        entityManager.clear();
    }


    @Test
    void 날씨_요약_알람이_모든_멤버들에게_한번에_성공한다() {

        // Given
        doNothing().when(weatherRefresherService).refresh(anySet());

        List<Long> memberIds = savedMembers.stream().map(Member::getId).toList();
        List<String> fcmTokens = savedAlarms.stream().map(Alarm::getFcmToken).toList();
        List<Long> locationIds = savedLocations.stream().map(Location::getId).toList();

        // When
        alarmService.trigger(AlarmType.WEATHER_SUMMARY);

        // Then
        verify(weatherSummaryMessageMaker, times(DUMMY_DATA_SIZE)).createAlarmMessage(alarmInfoArgumentCaptor.capture());
        for (AlarmInfo alarmInfo:alarmInfoArgumentCaptor.getAllValues()) {
            checkAlarmInfo(alarmInfo, memberIds, locationIds);
        }

        // 멤버들의 수(DUMMY_DATA_SIZE) 만큼 메시지 전송 시도
        verify(sender, times(DUMMY_DATA_SIZE)).send(notificationMessageArgumentCaptor.capture());
        for (NotificationMessage notificationMessage:notificationMessageArgumentCaptor.getAllValues()) {
            checkFcmMessage(notificationMessage, fcmTokens);
        }

        // 날씨 업데이트는 호출하지 않는다.
        verifyNoInteractions(weatherRefresherService);
    }

    private void checkFcmMessage(NotificationMessage notificationMessage, List<String> fcmTokens) {
        if (notificationMessage instanceof FcmMessage fcmMessage) {
            assertThat(fcmMessage)
                    .satisfies(message -> {
                        assertThat(message.getFcmToken()).isIn(fcmTokens);
                    });
        } else
            Assertions.fail("전송 메시지가 FcmMessage 타입이 아닙니다: " + notificationMessage.getClass());
    }

    private void checkAlarmInfo(AlarmInfo alarmInfo, List<Long> memberIds, List<Long> locationIds) {
        if (alarmInfo instanceof WeatherSummaryAlarmInfo weatherSummaryAlarmInfo) {
            assertThat(weatherSummaryAlarmInfo)
                    .satisfies(info -> {
                        assertThat(info.getAlarmMemberDTO().getId()).isIn(memberIds);
                        assertThat(info.getWeatherSummaryDTO().getLocationId()).isIn(locationIds);
                        assertThat(info.getExtraWeatherSummaryDTO().getLocationId()).isIn(locationIds);
                    });
        } else
            Assertions.fail("알림 정보가 WeatherSummaryAlarmInfo 타입이 아닙니다: " + alarmInfo.getClass());
    }


    @Test
    void 조회되는_멤버가_없는_경우_메시지가_작성되지_않는다() {

        // Given
        List<Alarm> allAlarm = alarmRepository.findAll();
        for (Alarm alarm : allAlarm) { alarm.getSummaryAlarmTimes().clear(); }

        entityManager.flush();
        entityManager.clear();

        // When
        alarmService.trigger(AlarmType.WEATHER_SUMMARY);

        // Then
        verifyNoInteractions(weatherSummaryMessageMaker, sender, weatherRefresherService);
    }

    @Test
    void 일부_멤버의_전송이_실패하고_재시도하지만_최종_실패한다() {

        // Given
        Member failedMember = savedMembers.get(0);
        Set<Long> failedLocationIds = Set.of(failedMember.getLocation().getId());
        weatherRepository.deleteAllByLocation(failedMember.getLocation());

        String failedFcmToken= savedAlarms.stream()
                .filter(alarm -> alarm.getMember().getId().equals(failedMember.getId()))
                .map(Alarm::getFcmToken)
                .toString();

        entityManager.flush();
        entityManager.clear();

        // 재시도를 위한 날씨 업데이트 실패
        doNothing().when(weatherRefresherService).refresh(eq(failedLocationIds));

        // When
        alarmService.trigger(AlarmType.WEATHER_SUMMARY);

        // Then
        // MessageMaker 호출 검증 (member1 1회 + member2 1회 + member3 2회 = 총 4회)
        verify(weatherSummaryMessageMaker, times(4)).createAlarmMessage(alarmInfoArgumentCaptor.capture());
        Assertions.assertThat(findRepeatedLocationIds()).isEqualTo(failedLocationIds);

        // Sender 호출 검증 (member1 1회 시도 + member2 1회 시도 + member3 0회 = 총 2회)
        verify(sender, times(2)).send(notificationMessageArgumentCaptor.capture());
        Assertions.assertThat(findSentFcmTokens()).doesNotContain(failedFcmToken);

        // weatherRefresher 호출 검증 (재시도 시 1번 호출)
        verify(weatherRefresherService, times(1)).refresh(eq(failedLocationIds));
 }

    private List<String> findSentFcmTokens() {
        return notificationMessageArgumentCaptor.getAllValues().stream()
                .filter(FcmMessage.class::isInstance)
                .map(msg -> ((FcmMessage) msg).getFcmToken())
                .toList();
    }

    private Set<Long> findRepeatedLocationIds() {
        return alarmInfoArgumentCaptor.getAllValues().stream()
                .filter(WeatherSummaryAlarmInfo.class::isInstance)
                .map(info -> ((WeatherSummaryAlarmInfo) info).getAlarmMemberDTO().getLocationId())
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet().stream()
                .filter(e -> e.getValue() > 1)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }


    @Test
    void 일부_멤버의_발송이_실패하고_재전송_시_성공한다() {

        // Given
        Member retryMember = savedMembers.get(0);
        Set<Long> retryLocationIds = Set.of(retryMember.getLocation().getId());
        weatherRepository.deleteAllByLocation(retryMember.getLocation());

        entityManager.flush();
        entityManager.clear();

        doAnswer(invocation -> {
                    for (int j = 0; j < DUMMY_DATA_SIZE; j++) {
                        Weather weather = Weather.builder()
                                .location(retryMember.getLocation())
                                .presentationTime(LocalDateTime.now().plusHours(j))
                                .temperature(15 + j)
                                .rainType(RainType.RAIN)
                                .humidity(50)
                                .windSpeed(12.0)
                                .build();
                        weatherRepository.saveAndFlush(weather);
                    }
                    return null;
                }
        ).when(weatherRefresherService).refresh(eq(retryLocationIds));


        // When
        alarmService.trigger(AlarmType.WEATHER_SUMMARY);

        // Then
        // MessageMaker 호출 검증 (member1 1회 + member2 1회 + member3 2회 = 총 4회)
        verify(weatherSummaryMessageMaker, times(4)).createAlarmMessage(alarmInfoArgumentCaptor.capture());
        Assertions.assertThat(findRepeatedLocationIds()).isEqualTo(retryLocationIds);

        // Sender 호출 검증 (member1 1회 시도 + member2 1회 시도 + member3 1회 = 총 3회)
        verify(sender, times(3)).send(notificationMessageArgumentCaptor.capture());

        List<String> expectedFcmTokens = savedAlarms.stream()
                .map(Alarm::getFcmToken)
                .toList();

        assertThat(findSentFcmTokens()).containsAll(expectedFcmTokens);

        // weatherRefresher 호출 검증 (재시도 시 1번 호출)
        verify(weatherRefresherService, times(1)).refresh(eq(retryLocationIds));
    }



    @Test
    @DirtiesContext(methodMode = MethodMode.BEFORE_METHOD)
    public void fcm토큰의_중복될_때_예외가_발생한다() throws Exception {

        Member member = saveMember();

        String authHeader = TestUtil.generateJwtToken(jwtUtil, member);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("fcmToken", "FcmToken1");

        ObjectMapper objectMapper = new ObjectMapper();

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



}