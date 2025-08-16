package org.pknu.weather.alarm.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pknu.weather.alarm.dto.LiveRainAlarmInfo;
import org.pknu.weather.alarm.enums.AlarmType;
import org.pknu.weather.alarm.event.LiveRainAlarmCreatedEvent;
import org.pknu.weather.alarm.message.AlarmMessageMaker;
import org.pknu.weather.alarm.repository.AlarmRepository;
import org.pknu.weather.alarm.sender.FcmMessage;
import org.pknu.weather.alarm.sender.NotificationMessage;
import org.pknu.weather.alarm.sender.NotificationSender;
import org.pknu.weather.alarm.service.AlarmCooldownService;
import org.pknu.weather.location.entity.Location;
import org.pknu.weather.post.entity.Post;
import org.pknu.weather.post.repository.PostRepository;

@ExtendWith(MockitoExtension.class)
class LiveRainAlarmHandlerTest {

    @Mock
    private AlarmRepository alarmRepository;
    @Mock
    private AlarmMessageMaker liveRainAlarmMessageMaker;
    @Mock
    private NotificationSender sender;
    @Mock
    private AlarmCooldownService alarmCooldownService;
    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private LiveRainAlarmHandler liveRainAlarmHandler;

    private Location testLocation;
    private Post testPost;
    private LiveRainAlarmCreatedEvent testEvent;

    @BeforeEach
    void setUp() {
        testLocation = Location.builder()
                .id(100L)
                .province("서울특별시")
                .city("마포구")
                .street("상수동")
                .build();

        testPost = Post.builder()
                .id(1L)
                .content("지금 비가 내리기 시작했습니다!")
                .location(testLocation)
                .build();

        testEvent = new LiveRainAlarmCreatedEvent(testPost.getId());

        when(postRepository.safeFindById(testEvent.getPostId())).thenReturn(testPost);

    }

    @Test
    @DisplayName("쿨다운 중이 아닌 사용자에게 알림이 전송되고 쿨다운이 설정되어야 한다")
    void handleRequest_SendsAlarmAndSetsCooldownToNonCooldownUsers() {

        // Given
        String fcmToken1 = "fcm_token_1";
        String fcmToken2 = "fcm_token_2";
        List<String> fcmTokens = Arrays.asList(fcmToken1, fcmToken2);
        NotificationMessage expectedMessage1 = new FcmMessage(fcmToken1, "실시간 비 알림", "☔️ 서울특별시 마포구 상수동에서 비가 온다는 소식이 공유되었습니다!");
        NotificationMessage expectedMessage2 = new FcmMessage(fcmToken2, "실시간 비 알림", "☔️ 서울특별시 마포구 상수동에서 비가 온다는 소식이 공유되었습니다!");

        when(alarmRepository.findLiveRainAlarmInfo(testLocation.getId())).thenReturn(fcmTokens);
        when(alarmCooldownService.isInCooldown(eq(AlarmType.RAIN_ALERT), any(String.class))).thenReturn(false);

        when(liveRainAlarmMessageMaker.createAlarmMessage(any(LiveRainAlarmInfo.class)))
                .thenAnswer(invocation -> {
                    LiveRainAlarmInfo info = invocation.getArgument(0);
                    if (info.getFcmToken().equals(fcmToken1)) return expectedMessage1;
                    if (info.getFcmToken().equals(fcmToken2)) return expectedMessage2;
                    return null;
                });

        // When
        liveRainAlarmHandler.handleRequest(testEvent);

        // Then
        verify(sender, times(1)).send(expectedMessage1);
        verify(sender, times(1)).send(expectedMessage2);

        verify(alarmCooldownService, times(1)).setCooldown(AlarmType.RAIN_ALERT, fcmToken1);
        verify(alarmCooldownService, times(1)).setCooldown(AlarmType.RAIN_ALERT, fcmToken2);

        ArgumentCaptor<LiveRainAlarmInfo> infoCaptor = ArgumentCaptor.forClass(LiveRainAlarmInfo.class);
        verify(liveRainAlarmMessageMaker, times(2)).createAlarmMessage(infoCaptor.capture());

        List<LiveRainAlarmInfo> capturedInfos = infoCaptor.getAllValues();
        assertThat(capturedInfos).hasSize(2)
                .extracting(LiveRainAlarmInfo::getFcmToken)
                .containsExactlyInAnyOrder(fcmToken1, fcmToken2);

        assertThat(capturedInfos).allMatch(info -> info.getProvince().equals("서울특별시") &&
                info.getCity().equals("마포구") &&
                info.getStreet().equals("상수동") &&
                info.getPostContent().equals(testPost.getContent()));
    }

    @Test
    @DisplayName("쿨다운 중인 사용자에게는 알림 전송 및 쿨다운 설정이 이루어지지 않아야 한다")
    void handleRequest_SkipsUsersInCooldown() {
        // Given
        String fcmTokenInCooldown = "fcm_token_cooldown";
        String fcmTokenNotInCooldown = "fcm_token_not_cooldown";
        List<String> fcmTokens = Arrays.asList(fcmTokenInCooldown, fcmTokenNotInCooldown);

        NotificationMessage expectedMessageForNotInCooldown = new FcmMessage(fcmTokenNotInCooldown, "실시간 비 알림", "☔️ 서울특별시 마포구 상수동에서 비가 온다는 소식이 공유되었습니다!");

        when(alarmRepository.findLiveRainAlarmInfo(testLocation.getId())).thenReturn(fcmTokens);

        when(alarmCooldownService.isInCooldown(AlarmType.RAIN_ALERT, fcmTokenInCooldown)).thenReturn(true);
        when(alarmCooldownService.isInCooldown(AlarmType.RAIN_ALERT, fcmTokenNotInCooldown)).thenReturn(false);

        when(liveRainAlarmMessageMaker.createAlarmMessage(any(LiveRainAlarmInfo.class)))
                .thenAnswer(invocation -> {
                    LiveRainAlarmInfo info = invocation.getArgument(0);
                    if (info.getFcmToken().equals(fcmTokenNotInCooldown)) {
                        return expectedMessageForNotInCooldown;
                    }
                    return null;
                });

        // When
        liveRainAlarmHandler.handleRequest(testEvent);

        // Then
        verify(sender, never()).send(argThat(message -> ((FcmMessage) message).getFcmToken().equals(fcmTokenInCooldown)));
        verify(alarmCooldownService, never()).setCooldown(AlarmType.RAIN_ALERT, fcmTokenInCooldown);

        verify(sender, times(1)).send(expectedMessageForNotInCooldown);
        verify(alarmCooldownService, times(1)).setCooldown(AlarmType.RAIN_ALERT, fcmTokenNotInCooldown);

        verify(liveRainAlarmMessageMaker, times(1))
                .createAlarmMessage(argThat(info -> ((LiveRainAlarmInfo) info).getFcmToken().equals(fcmTokenNotInCooldown)));

        verify(liveRainAlarmMessageMaker, never())
                .createAlarmMessage(argThat(info -> ((LiveRainAlarmInfo) info).getFcmToken().equals(fcmTokenInCooldown)));

    }

    @Test
    @DisplayName("FCM 토큰이 없을 경우 아무런 알림도 전송되지 않아야 한다")
    void handleRequest_NoAlarmsSent_WhenNoFcmTokens() {
        // Given
        when(alarmRepository.findLiveRainAlarmInfo(testLocation.getId())).thenReturn(Collections.emptyList());

        // When
        liveRainAlarmHandler.handleRequest(testEvent);

        // Then
        verify(sender, never()).send(any());
    }
}
