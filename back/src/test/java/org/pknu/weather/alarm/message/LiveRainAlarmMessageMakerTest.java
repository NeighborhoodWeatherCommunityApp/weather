package org.pknu.weather.alarm.message;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;
import org.pknu.weather.alarm.dto.AlarmInfo;
import org.pknu.weather.alarm.dto.LiveRainAlarmInfo;
import org.pknu.weather.alarm.dto.WeatherSummaryAlarmInfo;
import org.pknu.weather.alarm.enums.AlarmType;
import org.pknu.weather.alarm.message.LiveRainAlarmMessageMaker;
import org.pknu.weather.alarm.sender.FcmMessage;

class LiveRainAlarmMessageMakerTest {

    private LiveRainAlarmMessageMaker messageMaker = new LiveRainAlarmMessageMaker();


    @Test
    void 게시글_내용_없는_알림_메시지를_성공적으로_생성한다() {
        // Given
        LiveRainAlarmInfo info = LiveRainAlarmInfo.builder()
                .fcmToken("test_fcm_token_1")
                .province("서울특별시")
                .city("마포구")
                .street("상수동")
                .postContent(null) // 게시글 내용 없음
                .build();

        // When
        FcmMessage message = messageMaker.createAlarmMessage(info);


        // Then
        assertThat(message).isNotNull()
                .satisfies(msg -> {
                    assertThat(msg.getFcmToken()).isEqualTo("test_fcm_token_1");
                    assertThat(msg.getAlarmTitle()).isEqualTo(AlarmType.RAIN_ALERT.getDescription());
                    assertThat(msg.getAlarmMessage()).isNotNull();
                });
    }

    @Test
    void 게시글_내용_있는_알림_메시지를_성공적으로_생성한다() {
        // Given
        LiveRainAlarmInfo info = LiveRainAlarmInfo.builder()
                .fcmToken("test_fcm_token_2")
                .province("충청북도")
                .city("청주시")
                .street("서원구")
                .postContent("지금 천둥 번개 치면서 비가 엄청 쏟아져요!") // 게시글 내용 있음
                .build();

        // When
        FcmMessage message = messageMaker.createAlarmMessage(info);

        // Then

        assertThat(message).isNotNull()
                .satisfies(msg -> {
                    assertThat(msg.getFcmToken()).isEqualTo("test_fcm_token_2");
                    assertThat(msg.getAlarmTitle()).isEqualTo(AlarmType.RAIN_ALERT.getDescription());
                    assertThat(msg.getAlarmMessage()).contains("지금 천둥 번개 치면서 비가 엄청 쏟아져요!");
                });

    }


    @Test
    void AlarmInfo_타입이_LiveRainAlarmInfo가_아닐_경우_IllegalArgumentException이_발생한다() {
        // Given
        AlarmInfo wrongTypeInfo = new WeatherSummaryAlarmInfo(null,null,null);

        // When & Then
        assertThatThrownBy(() -> messageMaker.validate(wrongTypeInfo))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("LiveRainAlarmMessageMaker는 LiveRainAlarmInfo만 처리할 수 있습니다.");
    }

    @Test
    void fcmToken이_null일_경우_IllegalStateException이_발생한다() {
        // Given
        LiveRainAlarmInfo info = LiveRainAlarmInfo.builder()
                .fcmToken(null)
                .province("서울")
                .city("마포구")
                .street("상수동")
                .build();

        // When & Then
        assertThatThrownBy(() -> messageMaker.validate(info))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("fcmToken이 비어있습니다.");
    }

    @Test
    void fcmToken이_비어있을_경우_IllegalStateException이_발생한다() {
        // Given
        LiveRainAlarmInfo info = LiveRainAlarmInfo.builder()
                .fcmToken("") // 빈 fcmToken
                .province("서울")
                .city("마포구")
                .street("상수동")
                .build();

        // When & Then
        assertThatThrownBy(() -> messageMaker.validate(info))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("fcmToken이 비어있습니다.");
    }

    @Test
    void province가_비어있을_경우_IllegalStateException이_발생한다() {
        // Given
        LiveRainAlarmInfo info = LiveRainAlarmInfo.builder()
                .fcmToken("some_token")
                .province("") // 빈 province
                .city("마포구")
                .street("상수동")
                .build();

        // When & Then
        assertThatThrownBy(() -> messageMaker.validate(info))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void city가_비어있을_경우_IllegalStateException이_발생한다() {
        // Given
        LiveRainAlarmInfo info = LiveRainAlarmInfo.builder()
                .fcmToken("some_token")
                .province("서울")
                .city("") // 빈 city
                .street("상수동")
                .build();

        // When & Then
        assertThatThrownBy(() -> messageMaker.validate(info))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void street이_비어있을_경우_IllegalStateException이_발생한다() {
        // Given
        LiveRainAlarmInfo info = LiveRainAlarmInfo.builder()
                .fcmToken("some_token")
                .province("서울")
                .city("마포구")
                .street("") // 빈 street
                .build();

        // When & Then
        assertThatThrownBy(() -> messageMaker.validate(info))
                .isInstanceOf(IllegalStateException.class);
    }

}