package org.pknu.weather.service.message;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pknu.weather.domain.common.AlarmType;
import org.pknu.weather.service.dto.AlarmInfo;
import org.pknu.weather.service.dto.LiveRainAlarmInfo;
import org.pknu.weather.service.sender.FcmMessage;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LiveRainAlarmMessageMaker implements AlarmMessageMaker {

    @Override
    public FcmMessage createAlarmMessage(AlarmInfo alarmInfo) {

        validate(alarmInfo);

        return createLiveRainAlarm((LiveRainAlarmInfo) alarmInfo);
    }

    private FcmMessage createLiveRainAlarm(LiveRainAlarmInfo liveRainAlarmInfo) {

        String messageBody = getMessageBody(liveRainAlarmInfo);

        return new FcmMessage(liveRainAlarmInfo.getFcmToken(), AlarmType.RAIN_ALERT.getDescription(), messageBody);
    }

    private static String getMessageBody(LiveRainAlarmInfo liveRainAlarmInfo) {
        if(liveRainAlarmInfo.getPostContent() == null || liveRainAlarmInfo.getPostContent().isBlank())
            return "☔️ " + liveRainAlarmInfo.getFullAddress() + "에서 비가 온다는 소식이 공유되었습니다!";

        return "☔️ " + liveRainAlarmInfo.getFullAddress() + "에서 비가 온다는 글이 올라왔습니다!\n"
                + liveRainAlarmInfo.getPostContent();
    }

    @Override
    public void validate(AlarmInfo alarmInfo) {
        if (!(alarmInfo instanceof LiveRainAlarmInfo liveRainAlarmInfo)) {
            log.warn("유효성 검사 실패: LiveRainAlarmMessageMaker는 {} 타입만 처리 가능. 입력 타입: {}",
                    LiveRainAlarmMessageMaker.class.getSimpleName(),
                    alarmInfo != null ? alarmInfo.getClass().getName() : "null");
            throw new IllegalArgumentException("LiveRainAlarmMessageMaker는 LiveRainAlarmInfo만 처리할 수 있습니다.");
        }

        validateAlarmAgreement(liveRainAlarmInfo);
    }


    private void validateAlarmAgreement(LiveRainAlarmInfo info) {
        String fcmToken =  info.getFcmToken();
        String province =  info.getProvince();
        String city =  info.getCity();
        String street =  info.getStreet();

        if (fcmToken == null || fcmToken.isBlank()) {
            log.error("유효성 검사 실패: fcmToken이 비어있습니다.");
            throw new IllegalStateException("fcmToken이 비어있습니다.");
        }

        if (province == null || province.isBlank()) {
            log.error("유효성 검사 실패: 지역의 도/시(province)가 비어있습니다.");
            throw new IllegalStateException("지역의 도/시(province)가 비어있습니다.");
        }
        if (city == null || city.isBlank()) {
            log.error("유효성 검사 실패: 지역의 시/군/구(city)가 비어있습니다.");
            throw new IllegalStateException("지역의 시/군/구(city)가 비어있습니다.");
        }

        if (street == null || street.isBlank()) {
            log.error("유효성 검사 실패: 지역의 읍/면/동(street)가 비어있습니다.");
            throw new IllegalStateException("지역의 읍/면/동(street)가 비어있습니다.");
        }

    }
}
