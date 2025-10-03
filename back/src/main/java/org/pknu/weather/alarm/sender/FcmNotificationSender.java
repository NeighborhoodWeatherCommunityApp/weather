package org.pknu.weather.alarm.sender;

import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.ApnsConfig;
import com.google.firebase.messaging.Aps;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FcmNotificationSender implements NotificationSender {

    @Override
    public void send(NotificationMessage message) {

        if (!(message instanceof FcmMessage fcmMessage)) {
            log.warn("푸시 발송 실패: FcmSender는 FcmMessage만 지원. 입력 타입: {}",
                    message != null ? message.getClass().getName() : "null");
            throw new IllegalArgumentException("FcmSender는 FcmMessage만 지원합니다.");
        }

        try {
            FirebaseMessaging.getInstance().send(buildFcmMessage(fcmMessage));
            log.debug("FCM 메시지 전송 성공");
        } catch (FirebaseMessagingException e) {
            log.warn("푸시 전송 실패: {}", e.getMessage());
        }
    }

    private Message buildFcmMessage(FcmMessage message) {
        return Message.builder()
                .setToken(message.getFcmToken())
                .setNotification(Notification.builder()
                        .setTitle(message.getAlarmTitle())
                        .setBody(message.getAlarmMessage())
                        .build())
                .setAndroidConfig(AndroidConfig.builder()
                        .setPriority(AndroidConfig.Priority.HIGH)
                        .build())
                .setApnsConfig(ApnsConfig.builder()
                        .putHeader("apns-priority", "10")
                        .setAps(Aps.builder()
                                .setSound("default")
                                .build())
                        .build())
                .build();
    }
}
