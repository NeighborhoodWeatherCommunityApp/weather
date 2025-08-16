package org.pknu.weather.alarm.handler;


import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pknu.weather.location.entity.Location;
import org.pknu.weather.post.entity.Post;
import org.pknu.weather.alarm.enums.AlarmType;
import org.pknu.weather.alarm.event.LiveRainAlarmCreatedEvent;
import org.pknu.weather.alarm.repository.AlarmRepository;
import org.pknu.weather.post.repository.PostRepository;
import org.pknu.weather.alarm.service.AlarmCooldownService;
import org.pknu.weather.alarm.dto.LiveRainAlarmInfo;
import org.pknu.weather.alarm.message.AlarmMessageMaker;
import org.pknu.weather.alarm.sender.NotificationMessage;
import org.pknu.weather.alarm.sender.NotificationSender;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@Transactional
@RequiredArgsConstructor
public class LiveRainAlarmHandler implements ArgsAlarmHandler<LiveRainAlarmCreatedEvent>{

    private final AlarmRepository alarmRepository;
    private final AlarmMessageMaker liveRainAlarmMessageMaker;
    private final NotificationSender sender;
    private final AlarmCooldownService alarmCooldownService;
    private final PostRepository postRepository;

    @Override
    public AlarmType getAlarmType() {
        return AlarmType.RAIN_ALERT;
    }

    @Override
    public void handleRequest(LiveRainAlarmCreatedEvent event) {
        long postId = event.getPostId();
        Post post = postRepository.safeFindById(postId);
        Location location = post.getLocation();

        List<String> fcmTokens = alarmRepository.findLiveRainAlarmInfo(location.getId());

        fcmTokens.stream()
                .filter(fcmToken -> !alarmCooldownService.isInCooldown(getAlarmType(), fcmToken))
                .map(fcmToken -> getLiveRainAlarmInfo(post, location, fcmToken))
                .forEach(this::sendLiveRainAlarm);
    }

    private void sendLiveRainAlarm(LiveRainAlarmInfo info) {
        try {
            NotificationMessage message = liveRainAlarmMessageMaker.createAlarmMessage(info);
            sender.send(message);

            alarmCooldownService.setCooldown(getAlarmType(), info.getFcmToken());
        } catch (RuntimeException e) {
            log.warn("[실시간 비 알림 실패] fcmToken={}, reason={}", info.getFcmToken(), e.getMessage(), e);
        }
    }

    private static LiveRainAlarmInfo getLiveRainAlarmInfo(Post post, Location location, String fcmToken) {
        return LiveRainAlarmInfo.builder()
                .postContent(post.getContent())
                .fcmToken(fcmToken)
                .province(location.getProvince())
                .city(location.getCity())
                .street(location.getStreet())
                .build();
    }
}

