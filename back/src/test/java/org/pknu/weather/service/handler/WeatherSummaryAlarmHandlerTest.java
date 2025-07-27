package org.pknu.weather.service.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pknu.weather.weather.ExtraWeather;
import org.pknu.weather.domain.Location;
import org.pknu.weather.domain.common.SummaryAlarmTime;
import org.pknu.weather.dto.AlarmMemberDTO;
import org.pknu.weather.weather.dto.ExtraWeatherSummaryDTO;
import org.pknu.weather.weather.dto.WeatherSummaryDTO;
import org.pknu.weather.weather.repository.ExtraWeatherRepository;
import org.pknu.weather.repository.MemberRepository;
import org.pknu.weather.weather.repository.WeatherRepository;
import org.pknu.weather.service.dto.WeatherSummaryAlarmInfo;
import org.pknu.weather.service.message.AlarmMessageMaker;
import org.pknu.weather.service.sender.NotificationMessage;
import org.pknu.weather.service.sender.NotificationSender;
import org.pknu.weather.service.supports.AlarmTimeUtil;
import org.pknu.weather.weather.service.WeatherRefresherService;

@ExtendWith(MockitoExtension.class)
class WeatherSummaryAlarmHandlerTest {

    @InjectMocks
    private WeatherSummaryAlarmHandler weatherSummaryAlarmHandler;

    @Mock
    private MemberRepository memberRepository;
    @Mock
    private WeatherRepository weatherRepository;
    @Mock
    private ExtraWeatherRepository extraWeatherRepository;
    @Mock
    private AlarmMessageMaker weatherSummaryMessageMaker;
    @Mock
    private NotificationSender sender;
    @Mock
    private WeatherRefresherService weatherRefresherService;

    @Captor
    ArgumentCaptor<NotificationMessage> notificationMessageCaptor;


    @Nested
    @DisplayName("handleRequest 테스트")
    class HandleRequestTests {

        LocalDateTime fixedNow = LocalDateTime.of(2025, 5, 1, 7, 0);
        SummaryAlarmTime currentAlarmTime = SummaryAlarmTime.MORNING;

        MockedStatic<LocalDateTime> localDateTimeStaticMock;
        MockedStatic<AlarmTimeUtil> alarmTimeUtilStaticMock;

        List<AlarmMemberDTO> initialMembers = createAlarmMembers();
        List<WeatherSummaryDTO> initialWeatherSummaries = createWeatherSummaries();
        List<ExtraWeather> initialRawExtraWeathers = createExtraWeathers();

        @BeforeEach
        void setUpMocks() {
            localDateTimeStaticMock = Mockito.mockStatic(LocalDateTime.class);
            alarmTimeUtilStaticMock = Mockito.mockStatic(AlarmTimeUtil.class);
            localDateTimeStaticMock.when(LocalDateTime::now).thenReturn(fixedNow);
            alarmTimeUtilStaticMock.when(AlarmTimeUtil::getCurrentAlarmTime).thenReturn(currentAlarmTime);
        }

        @AfterEach
        void tearDownMocks() {
            localDateTimeStaticMock.close();
            alarmTimeUtilStaticMock.close();
        }

        @Test
        void 멤버가_없는_경우_메시지_작업을_수행하지_않는다() {

            // Given
            when(memberRepository.findMembersAndAlarmsByAlarmTime(currentAlarmTime))
                    .thenReturn(Collections.emptyList());

            // When
            weatherSummaryAlarmHandler.handleRequest();

            // Then
            verifyNoInteractions(weatherSummaryMessageMaker, sender, weatherRefresherService);

        }

        @Test
        void 멤버들의_전송이_모두_성공한다() {

            // Given
            NotificationMessage dummyMessage = mock(NotificationMessage.class);

            when(memberRepository.findMembersAndAlarmsByAlarmTime(currentAlarmTime)).thenReturn(initialMembers);
            when(weatherRepository.findWeatherSummary(anySet())).thenReturn(initialWeatherSummaries);
            when(extraWeatherRepository.findExtraWeatherByLocations(anySet(), eq(fixedNow.minusHours(4))))
                    .thenReturn(initialRawExtraWeathers);

            checkMessageMakerArgs(dummyMessage);

            doNothing().when(sender).send(any(NotificationMessage.class));

            // When
            weatherSummaryAlarmHandler.handleRequest();

            // Then
            verify(memberRepository).findMembersAndAlarmsByAlarmTime(any(SummaryAlarmTime.class));
            verify(extraWeatherRepository).findExtraWeatherByLocations(anySet(), eq(fixedNow.minusHours(4)));
            verify(weatherSummaryMessageMaker, times(initialMembers.size())).createAlarmMessage(any());
            verify(sender, times(initialMembers.size())).send(any());
        }


        @Test
        void 일부_멤버의_알람발송이_모두_실패한다() {
            // member2의 발송이 실패했다고 가정했을 때 테스트
            int member2Id = 1;

            // Given
            // 재시도 시 사용할 테스트 데이터
            Set<Long> retryLocationIds = new HashSet<>(List.of(102L)); // 실패한 멤버 지역 ID
            List<WeatherSummaryDTO> retryWeatherSummaries = List.of(initialWeatherSummaries.get(member2Id));
            List<ExtraWeather> retryRawExtraWeathers = List.of(initialRawExtraWeathers.get(member2Id));

            NotificationMessage dummyMessage1 = mock(NotificationMessage.class); // member1 초기 발송 메시지
            NotificationMessage dummyMessage2_initial = mock(NotificationMessage.class); // member2 초기 발송 메시지
            NotificationMessage dummyMessage2_retry = mock(NotificationMessage.class); // member2 재시도 발송 메시지

            when(memberRepository.findMembersAndAlarmsByAlarmTime(currentAlarmTime)).thenReturn(initialMembers);

            when(weatherRepository.findWeatherSummary(anySet()))
                    .thenReturn(initialWeatherSummaries)
                    .thenReturn(retryWeatherSummaries);

            when(extraWeatherRepository.findExtraWeatherByLocations(anySet(), any(LocalDateTime.class)))
                    .thenReturn(initialRawExtraWeathers)
                    .thenReturn(retryRawExtraWeathers);

            when(weatherSummaryMessageMaker.createAlarmMessage(argThat(info ->
                    info instanceof WeatherSummaryAlarmInfo &&
                            ((WeatherSummaryAlarmInfo) info).getAlarmMemberDTO() != null &&
                            ((WeatherSummaryAlarmInfo) info).getAlarmMemberDTO().getId().equals(1L)
            ))).thenReturn(dummyMessage1);

            when(weatherSummaryMessageMaker.createAlarmMessage(argThat(info ->
                    info instanceof WeatherSummaryAlarmInfo &&
                            ((WeatherSummaryAlarmInfo) info).getAlarmMemberDTO() != null &&
                            ((WeatherSummaryAlarmInfo) info).getAlarmMemberDTO().getId().equals(2L)
            )))
                    .thenReturn(dummyMessage2_initial)
                    .thenReturn(dummyMessage2_retry);

            doNothing() // member1 초기 발송 성공
                    .doThrow(new IllegalStateException("member2 초기 발송 실패"))
                    .doThrow(new IllegalStateException("member2 재시도 발송 실패"))
                    .when(sender).send(any(NotificationMessage.class));
            doNothing().when(weatherRefresherService).refresh(retryLocationIds);

            // When
            weatherSummaryAlarmHandler.handleRequest();

            // Then
            verify(memberRepository, times(1)).findMembersAndAlarmsByAlarmTime(currentAlarmTime);
            verify(weatherRepository, times(2)).findWeatherSummary(anySet());
            verify(extraWeatherRepository, times(2)).findExtraWeatherByLocations(anySet(), any(LocalDateTime.class));
            verify(weatherSummaryMessageMaker, times(3)).createAlarmMessage(any(WeatherSummaryAlarmInfo.class));
            verify(sender, times(3)).send(any(NotificationMessage.class));
            verify(weatherRefresherService, times(1)).refresh(retryLocationIds);

            // 각 멤버별 호출 횟수도 검증
            verify(weatherSummaryMessageMaker, times(1)).createAlarmMessage(argThat(info ->
                    info instanceof WeatherSummaryAlarmInfo && // 타입 체크 추가
                            ((WeatherSummaryAlarmInfo) info).getAlarmMemberDTO() != null &&
                            ((WeatherSummaryAlarmInfo) info).getAlarmMemberDTO().getId().equals(1L)
            ));

            verify(weatherSummaryMessageMaker, times(2)).createAlarmMessage(argThat(info ->
                    info instanceof WeatherSummaryAlarmInfo && // 타입 체크 추가
                            ((WeatherSummaryAlarmInfo) info).getAlarmMemberDTO() != null &&
                            ((WeatherSummaryAlarmInfo) info).getAlarmMemberDTO().getId().equals(2L)
            ));
        }

        @Test
        void 날씨_요약_데이터가_누락된_경우_해당_멤버_발송은_실패_목록에_추가된_후_재시도한다() {

            // Given
            List<WeatherSummaryDTO> initialWeatherSummaries = List.of(this.initialWeatherSummaries.get(0));
            List<WeatherSummaryDTO> retryWeatherSummariesFor = List.of(this.initialWeatherSummaries.get(1));
            List<ExtraWeather> retryRawExtraWeathers = List.of(initialRawExtraWeathers.get(1));
            Set<Long> retryLocationIds = new HashSet<>(List.of(102L));

            NotificationMessage dummyMessage1 = mock(NotificationMessage.class);

            // Given
            when(memberRepository.findMembersAndAlarmsByAlarmTime(currentAlarmTime)).thenReturn(initialMembers);

            // weatherRepository: 초기 호출 ({101, 102} 예상) -> 재시도 호출 ({102} 예상)
            when(weatherRepository.findWeatherSummary(anySet()))
                    .thenReturn(initialWeatherSummaries)
                    .thenReturn(retryWeatherSummariesFor);

            // extraWeatherRepository: 초기 호출 ({101, 102} 예상) -> 재시도 호출 ({102} 예상)
            when(extraWeatherRepository.findExtraWeatherByLocations(anySet(), eq(fixedNow.minusHours(4))))
                    .thenReturn(initialRawExtraWeathers)
                    .thenReturn(retryRawExtraWeathers);

            when(weatherSummaryMessageMaker.createAlarmMessage(argThat(info -> {
                if (!(info instanceof WeatherSummaryAlarmInfo specificInfo)) {
                    return false;
                }
                if (!specificInfo.getAlarmMemberDTO().getId().equals(1L)) {
                    return false;
                }
                return specificInfo.getWeatherSummaryDTO() != null;
            }))).thenReturn(dummyMessage1);

            when(weatherSummaryMessageMaker.createAlarmMessage(argThat(info -> {
                if (!(info instanceof WeatherSummaryAlarmInfo specificInfo)) {
                    return false;
                }
                if (!specificInfo.getAlarmMemberDTO().getId().equals(2L)) {
                    return false;
                }
                return specificInfo.getWeatherSummaryDTO() == null;
            }))).thenThrow(new IllegalStateException("WeatherSummaryDTO is null"));

            when(weatherSummaryMessageMaker.createAlarmMessage(argThat(info -> {
                if (!(info instanceof WeatherSummaryAlarmInfo specificInfo)) {
                    return false;
                }
                if (!specificInfo.getAlarmMemberDTO().getId().equals(2L)) {
                    return false;
                }
                return specificInfo.getWeatherSummaryDTO() != null;
            }))).thenReturn(dummyMessage1);

            doNothing().when(sender).send(eq(dummyMessage1));

            doNothing().when(weatherRefresherService).refresh(retryLocationIds);

            // When
            weatherSummaryAlarmHandler.handleRequest();

            // Then
            verify(memberRepository, times(1)).findMembersAndAlarmsByAlarmTime(currentAlarmTime);

            // Repository 호출 검증 (초기 1회 + 재시도 1회 = 총 2회)
            verify(weatherRepository, times(2)).findWeatherSummary(anySet());
            verify(extraWeatherRepository, times(2)).findExtraWeatherByLocations(anySet(), any(LocalDateTime.class));

            // MessageMaker 호출 검증 (member1 성공 1회, member2 2회 시도 = 총 3회)
            verify(weatherSummaryMessageMaker, times(3)).createAlarmMessage(any(WeatherSummaryAlarmInfo.class));

            // member1에 대한 호출 검증
            verify(weatherSummaryMessageMaker, times(1)).createAlarmMessage(argThat(info ->
                    info instanceof WeatherSummaryAlarmInfo specificInfo &&
                            specificInfo.getAlarmMemberDTO().getId().equals(1L) &&
                            specificInfo.getWeatherSummaryDTO() != null
            ));

            // member2에 대한 호출 검증
            verify(weatherSummaryMessageMaker, times(1)).createAlarmMessage(argThat(info -> // 초기, 재시도 총 2번 호출 시도
                    info instanceof WeatherSummaryAlarmInfo specificInfo &&
                            specificInfo.getAlarmMemberDTO().getId().equals(2L) &&
                            specificInfo.getWeatherSummaryDTO() == null
            ));

            // member2에 대한 호출 검증
            verify(weatherSummaryMessageMaker, times(1)).createAlarmMessage(argThat(info -> // 초기, 재시도 총 2번 호출 시도
                    info instanceof WeatherSummaryAlarmInfo specificInfo &&
                            specificInfo.getAlarmMemberDTO().getId().equals(2L) &&
                            specificInfo.getWeatherSummaryDTO() != null
            ));

            // Sender 호출 검증
            verify(sender, times(2)).send(eq(dummyMessage1));
            verifyNoMoreInteractions(sender);

            // weatherRefresher 호출 검증
            verify(weatherRefresherService, times(1)).refresh(retryLocationIds);

        }

        @Test
        void 추가_날씨_요약_데이터가_누락된_경우_해당_멤버_발송은_실패_목록에_추가된다() {
            // Given
            List<ExtraWeather> initialRawExtraWeathers = List.of(this.initialRawExtraWeathers.get(0));
            List<ExtraWeather> retryRawExtraWeathers = List.of(this.initialRawExtraWeathers.get(1));

            List<WeatherSummaryDTO> retryWeatherSummaries = List.of(initialWeatherSummaries.get(1));
            Set<Long> retryLocationIds = new HashSet<>(List.of(102L));


            NotificationMessage dummyMessage = mock(NotificationMessage.class); //

            // Given
            when(memberRepository.findMembersAndAlarmsByAlarmTime(currentAlarmTime)).thenReturn(initialMembers);

            // weatherRepository: 초기 호출 ({101, 102} 예상) -> 재시도 호출 ({102} 예상)
            when(weatherRepository.findWeatherSummary(anySet()))
                    .thenReturn(initialWeatherSummaries)
                    .thenReturn(retryWeatherSummaries);

            // extraWeatherRepository: 초기 호출 ({101, 102} 예상) -> 재시도 호출 ({102} 예상)
            when(extraWeatherRepository.findExtraWeatherByLocations(anySet(), eq(fixedNow.minusHours(4))))
                    .thenReturn(initialRawExtraWeathers)
                    .thenReturn(retryRawExtraWeathers);

            // member1 메시지 생성
            when(weatherSummaryMessageMaker.createAlarmMessage(argThat(info ->
                    info instanceof WeatherSummaryAlarmInfo specificInfo &&
                            specificInfo.getAlarmMemberDTO().getId().equals(1L) &&
                            specificInfo.getExtraWeatherSummaryDTO() != null
            ))).thenReturn(dummyMessage);

            // member2 메시지 생성
            when(weatherSummaryMessageMaker.createAlarmMessage(argThat(info -> {
                if (!(info instanceof WeatherSummaryAlarmInfo specificInfo)) {
                    return false;
                }
                if (!specificInfo.getAlarmMemberDTO().getId().equals(2L)) {
                    return false;
                }
                return specificInfo.getExtraWeatherSummaryDTO() == null;
            }))).thenThrow(new IllegalArgumentException("ExtraWeatherSummaryDTO is null"));

            // member2 재시도 메시지 생성
            when(weatherSummaryMessageMaker.createAlarmMessage(argThat(info -> {
                if (!(info instanceof WeatherSummaryAlarmInfo specificInfo)) {
                    return false;
                }
                if (!specificInfo.getAlarmMemberDTO().getId().equals(2L)) {
                    return false;
                }
                return specificInfo.getExtraWeatherSummaryDTO() != null;
            }))).thenReturn(dummyMessage);

            doNothing().when(sender).send(eq(dummyMessage));

            doNothing().when(weatherRefresherService).refresh(retryLocationIds);

            // When
            weatherSummaryAlarmHandler.handleRequest();

            // Then
            verify(memberRepository, times(1)).findMembersAndAlarmsByAlarmTime(currentAlarmTime);

            // Repository 호출 검증 (초기 1회 + 재시도 1회 = 총 2회)
            verify(weatherRepository, times(2)).findWeatherSummary(anySet());
            verify(extraWeatherRepository, times(2)).findExtraWeatherByLocations(anySet(), any(LocalDateTime.class));

            // MessageMaker 호출 검증 (member1 성공 1회, member2 2회 시도 = 총 3회)
            verify(weatherSummaryMessageMaker, times(3)).createAlarmMessage(any(WeatherSummaryAlarmInfo.class));
            // member1에 대한 호출 검증 (extra weather DTO 존재)
            verify(weatherSummaryMessageMaker, times(1)).createAlarmMessage(argThat(info ->
                    info instanceof WeatherSummaryAlarmInfo specificInfo &&
                            specificInfo.getAlarmMemberDTO().getId().equals(1L) &&
                            specificInfo.getExtraWeatherSummaryDTO() != null
            ));

            // member2에 대한 호출 검증
            verify(weatherSummaryMessageMaker, times(1)).createAlarmMessage(argThat(info ->
                    info instanceof WeatherSummaryAlarmInfo specificInfo &&
                            specificInfo.getAlarmMemberDTO().getId().equals(2L) &&
                            specificInfo.getExtraWeatherSummaryDTO() == null
            ));
            // member2에 대한 재시도 호출 검증
            verify(weatherSummaryMessageMaker, times(1)).createAlarmMessage(argThat(info ->
                    info instanceof WeatherSummaryAlarmInfo specificInfo &&
                            specificInfo.getAlarmMemberDTO().getId().equals(2L) &&
                            specificInfo.getExtraWeatherSummaryDTO() != null
            ));

            verify(sender, times(2)).send(eq(dummyMessage));
            verifyNoMoreInteractions(sender);

            verify(weatherRefresherService, times(1)).refresh(retryLocationIds);
        }

        @Test
        void FCM_토큰이_없는_멤버는_알람_발송에_실패하고_실패_목록에_추가된다() {
            // Given
            // 멤버 3명: member1(valid token, loc101), member2(loc102, null token), member3(loc103, empty token)
            List<AlarmMemberDTO> members = List.of(
                    createAlarmMemberDTO(1L, 101L, "token1", true, true, true, true),
                    createAlarmMemberDTO(2L, 102L, null, true, true, true, true), // null 토큰
                    createAlarmMemberDTO(3L, 103L, "", true, true, true, true)    // empty 토큰
            );

            // 모든 지역(loc101, loc102, loc103)에 대한 날씨 및 추가 날씨 데이터가 있다고 가정
            List<WeatherSummaryDTO> initialWeatherSummaries = List.of(
                    createWeatherSummaryDTO(101L, "맑음", 25, 15),
                    createWeatherSummaryDTO(102L, "비", 20, 10),
                    createWeatherSummaryDTO(103L, "구름 많음", 22, 12)
            );

            List<ExtraWeather> initialRawExtraWeathers = List.of(
                    buildExtraWeather(101L, fixedNow.minusHours(1), 5, 1,1,1,1,1,1,1,1,1,1,1,1),
                    buildExtraWeather(102L, fixedNow.minusHours(2), 3,  1,1,1,1,1,1,1,1,1,1,1,1),
                    buildExtraWeather(103L, fixedNow.minusHours(3), 4,  1,1,1,1,1,1,1,1,1,1,1,1)
            );

            // 재시도 대상 (member2 -> loc102, member3 -> loc103)
            Set<Long> retryLocationIds = new HashSet<>(List.of(102L, 103L));

            List<WeatherSummaryDTO> retryWeatherSummaries = initialWeatherSummaries.stream()
                    .skip(1)
                    .collect(Collectors.toList());

            List<ExtraWeather> retryRawExtraWeathers = initialRawExtraWeathers.stream()
                    .skip(1)
                    .collect(Collectors.toList());

            NotificationMessage dummyMessage1 = mock(NotificationMessage.class); // member1 메시지
            NotificationMessage dummyMessage2_initial = mock(NotificationMessage.class); // member2 초기 메시지
            NotificationMessage dummyMessage3_initial = mock(NotificationMessage.class); // member3 초기 메시지
            NotificationMessage dummyMessage2_retry = mock(NotificationMessage.class); // member2 재시도 메시지
            NotificationMessage dummyMessage3_retry = mock(NotificationMessage.class); // member3 재시도 메시지

            when(memberRepository.findMembersAndAlarmsByAlarmTime(currentAlarmTime)).thenReturn(members);

            // weatherRepository: 초기 호출 (모든 지역) -> 재시도 호출 (실패한 지역들)
            when(weatherRepository.findWeatherSummary(anySet()))
                    .thenReturn(initialWeatherSummaries)
                    .thenReturn(retryWeatherSummaries); // 재시도 시 실패한 loc102, 103 데이터 반환

            // extraWeatherRepository: 초기 호출 (모든 지역) -> 재시도 호출 (실패한 지역들)
            when(extraWeatherRepository.findExtraWeatherByLocations(anySet(), any(LocalDateTime.class)))
                    .thenReturn(initialRawExtraWeathers)
                    .thenReturn(retryRawExtraWeathers); // 재시도 시 실패한 loc102, 103 데이터 반환

            // MessageMaker stubbing - 초기 및 재시도 모두 포함 (총 5회 호출 예상)

            // member1 메시지 생성: 1회 호출
            when(weatherSummaryMessageMaker.createAlarmMessage(argThat(info ->
                    info instanceof WeatherSummaryAlarmInfo && ((WeatherSummaryAlarmInfo) info).getAlarmMemberDTO()
                            .getId().equals(1L)
            ))).thenReturn(dummyMessage1);

            // member2 메시지 생성: 총 2회 호출
            when(weatherSummaryMessageMaker.createAlarmMessage(argThat(info ->
                    info instanceof WeatherSummaryAlarmInfo && ((WeatherSummaryAlarmInfo) info).getAlarmMemberDTO()
                            .getId().equals(2L)
            ))).thenReturn(dummyMessage2_initial)
                    .thenReturn(dummyMessage2_retry);

            // member3 메시지 생성: 총 2회 호출
            when(weatherSummaryMessageMaker.createAlarmMessage(argThat(info ->
                    info instanceof WeatherSummaryAlarmInfo && ((WeatherSummaryAlarmInfo) info).getAlarmMemberDTO()
                            .getId().equals(3L)
            ))).thenReturn(dummyMessage3_initial)
                    .thenReturn(dummyMessage3_retry);

            doNothing() // member1 초기 성공
                    .doThrow(new RuntimeException("Invalid token - member2 initial")) // member2 초기 실패 (null token)
                    .doThrow(new RuntimeException("Invalid token - member3 initial")) // member3 초기 실패 (empty token)
                    .doThrow(new RuntimeException("Invalid token - member2 retry")) // member2 재시도 실패
                    .doThrow(new RuntimeException("Invalid token - member3 retry")) // member3 재시도 실패
                    .when(sender).send(any(NotificationMessage.class));

            doNothing().when(weatherRefresherService).refresh(retryLocationIds);

            // When
            weatherSummaryAlarmHandler.handleRequest();

            // Then
            // 초기 멤버 조회 1회
            verify(memberRepository, times(1)).findMembersAndAlarmsByAlarmTime(currentAlarmTime);

            // Repository 호출 검증 (초기 1회 + 재시도 1회 = 총 2회)
            verify(weatherRepository, times(2)).findWeatherSummary(anySet());
            verify(extraWeatherRepository, times(2)).findExtraWeatherByLocations(anySet(), any(LocalDateTime.class));

            // MessageMaker 호출 검증 (member1 1회 + member2 2회 + member3 2회 = 총 5회)
            verify(weatherSummaryMessageMaker, times(5)).createAlarmMessage(any(WeatherSummaryAlarmInfo.class));

            // Sender 호출 검증 (총 5회 시도)
            verify(sender, times(5)).send(any(NotificationMessage.class));

            // weatherRefresher 호출 검증
            verify(weatherRefresherService, times(1)).refresh(retryLocationIds);
        }

        private List<AlarmMemberDTO> createAlarmMembers() {
            return List.of(
                    createAlarmMemberDTO(1L, 101L, "token1", true, true, false, false),
                    createAlarmMemberDTO(2L, 102L, "token2", true, false, true, false)
            );
        }

        private List<WeatherSummaryDTO> createWeatherSummaries() {
            return List.of(
                    createWeatherSummaryDTO(101L, "맑음", 25, 15),
                    createWeatherSummaryDTO(102L, "비", 20, 10)
            );
        }

        private List<ExtraWeather> createExtraWeathers() {
            LocalDateTime baseTime1 = fixedNow.minusHours(1);
            LocalDateTime baseTime2 = fixedNow.minusHours(2);

            return List.of(
                    buildExtraWeather(101L, baseTime1, 2, 5, 6, 7, 8, 6, 4, 2, 1, 2, 35, 1, 12),
                    buildExtraWeather(102L, baseTime2, 1, 2, 3, 4, 5, 6, 7, null, 2, 3, 55, 2, 22)
            );
        }

        private void checkMessageMakerArgs(NotificationMessage dummyMessage) {
            when(weatherSummaryMessageMaker.createAlarmMessage(argThat(info -> {
                if (!(info instanceof WeatherSummaryAlarmInfo specificInfo)) {
                    return false;
                }

                Long expectedLocationId = specificInfo.getAlarmMemberDTO().getLocationId();
                Long weatherLocationId = specificInfo.getWeatherSummaryDTO().getLocationId();
                Long extraWeatherLocationId = specificInfo.getExtraWeatherSummaryDTO().getLocationId();

                return expectedLocationId.equals(weatherLocationId) &&
                        expectedLocationId.equals(extraWeatherLocationId);
            }))).thenReturn(dummyMessage);
        }

    }

    @Nested
    @DisplayName("알람 전송 테스트")
    class SendSingleAlarmTests {

        private AlarmMemberDTO testMember;

        private WeatherSummaryDTO testWeather;
        private ExtraWeatherSummaryDTO testExtraSummary;
        private final Long testLocationId = 201L;
        String errorMessage = "메시지 생성 실패";


        @BeforeEach
        void setUpSingleAlarm() {
            testMember = createAlarmMemberDTO(1L, testLocationId, "test-token", true, true, true, true);
            testWeather = createWeatherSummaryDTO(testLocationId, "맑음", 28, 18);
            testExtraSummary = createExtraWeatherSummaryDTO(testLocationId, 35, 8, "13");
        }

        @Test
        void 메시지_생성_및_발송_성공_테스트() {
            // Given
            NotificationMessage dummyMessage = mock(NotificationMessage.class);

            when(weatherSummaryMessageMaker.createAlarmMessage(argThat(info -> {
                if (!(info instanceof WeatherSummaryAlarmInfo specificInfo)) {
                    return false;
                }

                return specificInfo.getWeatherSummaryDTO().equals(testWeather) &&
                        specificInfo.getExtraWeatherSummaryDTO().equals(testExtraSummary) &&
                        specificInfo.getAlarmMemberDTO().equals(testMember);
            }))).thenReturn(dummyMessage);

            doNothing().when(sender).send(any(NotificationMessage.class));

            // When
            boolean result = weatherSummaryAlarmHandler.sendSingleAlarm(testLocationId, testMember, testWeather,
                    testExtraSummary);

            // Then
            assertThat(result).isTrue();
            verify(weatherSummaryMessageMaker).createAlarmMessage(any(WeatherSummaryAlarmInfo.class));
            verify(sender).send(notificationMessageCaptor.capture());
            assertThat(notificationMessageCaptor.getValue()).isEqualTo(dummyMessage);
        }

        @Test
        void 메시지_생성_중_IllegalStateException_발생_시_발송을_하지_않는다() {
            // Given
            when(weatherSummaryMessageMaker.createAlarmMessage(any(WeatherSummaryAlarmInfo.class))).thenThrow(
                    new IllegalStateException(errorMessage));

            // When
            boolean result = weatherSummaryAlarmHandler.sendSingleAlarm(testLocationId, testMember, testWeather,
                    testExtraSummary);

            // Then
            assertThat(result).isFalse();
            verify(weatherSummaryMessageMaker).createAlarmMessage(any(WeatherSummaryAlarmInfo.class));
            verifyNoInteractions(sender);
        }

        @Test
        void 메시지_생성_중_IllegalArgumentException_발생_시_발송을_하지_않는다() {
            // Given
            when(weatherSummaryMessageMaker.createAlarmMessage(any(WeatherSummaryAlarmInfo.class))).thenThrow(
                    new IllegalArgumentException(errorMessage));

            // When
            boolean result = weatherSummaryAlarmHandler.sendSingleAlarm(testLocationId, testMember, testWeather,
                    testExtraSummary);

            // Then
            assertThat(result).isFalse();
            verify(weatherSummaryMessageMaker).createAlarmMessage(any(WeatherSummaryAlarmInfo.class));
            verifyNoInteractions(sender);
        }

        @Test
        void 메시지_발송_중_예외가_발생한다() {
            // Given
            NotificationMessage dummyMessage = mock(NotificationMessage.class);
            when(weatherSummaryMessageMaker.createAlarmMessage(any(WeatherSummaryAlarmInfo.class))).thenReturn(
                    dummyMessage);

            doThrow(new RuntimeException(errorMessage)).when(sender)
                    .send(any(NotificationMessage.class));

            // When
            boolean result = weatherSummaryAlarmHandler.sendSingleAlarm(testLocationId, testMember, testWeather,
                    testExtraSummary);

            // Then
            assertThat(result).isFalse();
            verify(weatherSummaryMessageMaker).createAlarmMessage(any(WeatherSummaryAlarmInfo.class));
            verify(sender).send(any(NotificationMessage.class));
        }
    }

    private AlarmMemberDTO createAlarmMemberDTO(Long memberId, Long locationId, String fcmToken, boolean agreePrecip,
                                                boolean agreeTemp, boolean agreeDust, boolean agreeUv) {
        return AlarmMemberDTO.builder()
                .id(memberId)
                .locationId(locationId)
                .fcmToken(fcmToken)
                .agreeTempAlarm(agreeTemp)
                .agreePrecipAlarm(agreePrecip)
                .agreeUvAlarm(agreeUv)
                .agreeDustAlarm(agreeDust)
                .build();
    }

    private WeatherSummaryDTO createWeatherSummaryDTO(Long locationId, String rainStatus, Integer maxTemp,
                                                      Integer minTemp) {
        return WeatherSummaryDTO.builder()
                .locationId(locationId)
                .rainStatus(rainStatus)
                .maxTemp(maxTemp)
                .minTemp(minTemp)
                .build();
    }

    private ExtraWeatherSummaryDTO createExtraWeatherSummaryDTO(Long locationId, Integer pm10, Integer maxUvValue,
                                                                String maxUvTime) {
        return ExtraWeatherSummaryDTO.builder()
                .locationId(locationId)
                .pm10(pm10)
                .maxUvTime(maxUvTime)
                .maxUvValue(maxUvValue)
                .build();
    }

    private ExtraWeather buildExtraWeather(Long locationId, LocalDateTime basetime, Integer uv, Integer uvPlus3,
                                           Integer uvPlus6, Integer uvPlus9, Integer uvPlus12, Integer uvPlus15,
                                           Integer uvPlus18, Integer uvPlus21, Integer o3, Integer pm10,
                                           Integer pm10value, Integer pm25, Integer pm25value) {

        Location location = createLocationMock(locationId);

        return ExtraWeather.builder()
                .id(1L)
                .location(location)
                .basetime(basetime)
                .uv(uv)
                .uvPlus3(uvPlus3)
                .uvPlus6(uvPlus6)
                .uvPlus9(uvPlus9)
                .uvPlus12(uvPlus12)
                .uvPlus15(uvPlus15)
                .uvPlus18(uvPlus18)
                .uvPlus21(uvPlus21)
                .o3(o3)
                .pm10(pm10)
                .pm10value(pm10value)
                .pm25(pm25)
                .pm25value(pm25value)
                .build();
    }

    private Location createLocationMock(Long locationId) {
        Location location = mock(Location.class);
        when(location.getId()).thenReturn(locationId);
        return location;
    }
}
