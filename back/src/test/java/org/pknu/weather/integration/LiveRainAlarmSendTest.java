package org.pknu.weather.integration;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import jakarta.persistence.EntityManager;
import java.util.Objects;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.pknu.weather.config.EmbeddedRedisConfig;
import org.pknu.weather.domain.Alarm;
import org.pknu.weather.domain.Location;
import org.pknu.weather.member.entity.Member;
import org.pknu.weather.domain.common.AlarmType;
import org.pknu.weather.domain.tag.DustTag;
import org.pknu.weather.domain.tag.HumidityTag;
import org.pknu.weather.domain.tag.SkyTag;
import org.pknu.weather.domain.tag.TemperatureTag;
import org.pknu.weather.domain.tag.WindTag;
import org.pknu.weather.dto.PostRequest.CreatePost;
import org.pknu.weather.repository.AlarmRepository;
import org.pknu.weather.repository.LocationRepository;
import org.pknu.weather.member.repository.MemberRepository;
import org.pknu.weather.service.AlarmCooldownService;
import org.pknu.weather.service.PostService;
import org.pknu.weather.service.sender.FcmMessage;
import org.pknu.weather.service.sender.NotificationSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@Import(EmbeddedRedisConfig.class)
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
public class LiveRainAlarmSendTest {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private PostService postService;
    @SpyBean
    private AlarmCooldownService alarmCooldownService;
    @SpyBean
    private NotificationSender sender;

    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private AlarmRepository alarmRepository;
    @Autowired
    private EntityManager entityManager;

    private Member postMember;
    private Alarm savedAlarm;

    @BeforeEach
    void setUp() {
        Objects.requireNonNull(redisTemplate.getConnectionFactory()).getConnection().serverCommands().flushAll();

        Location location = Location.builder()
                .province("testProvince")
                .city("testCity")
                .street("testStreet")
                .build();

        locationRepository.save(location);
        Member member1 = Member.builder()
                .email( UUID.randomUUID() + "testEmail1")
                .location(location)
                .build();
        postMember = memberRepository.save(member1);
        Member member2 = Member.builder()
                .email( UUID.randomUUID() + "testEmail2")
                .location(location)
                .build();
        Member savedMember = memberRepository.save(member2);

        Alarm alarm = Alarm.builder()
                .member(savedMember)
                .fcmToken("testFcmToken")
                .agreeUvAlarm(true)
                .agreePrecipAlarm(true)
                .agreeTempAlarm(true)
                .agreeDustAlarm(true)
                .agreeLiveRainAlarm(true)
                .build();

        savedAlarm = alarmRepository.save(alarm);

        entityManager.flush();
        entityManager.clear();
    }


    @Test
    void 비가_온다는_태그_저장시_같은_지역의_사용자에게_알림이_전송된다() {
        String email = postMember.getEmail();
        CreatePost createPost = CreatePost.builder()
                .content("testContent")
                .dustTag(DustTag.NORMAL)
                .humidityTag(HumidityTag.PLEASANT)
                .windTag(WindTag.NONE)
                .temperatureTag(TemperatureTag.COMMON)
                .skyTag(SkyTag.RAIN)
                .build();

        postService.createWeatherPost(email, createPost);
        TestTransaction.flagForCommit();
        TestTransaction.end();

        await().atMost(5, SECONDS).untilAsserted(() -> {
            verify(alarmCooldownService, times(1)).setCooldown(AlarmType.RAIN_ALERT, savedAlarm.getFcmToken());
            verify(sender, times(1)).send(any(FcmMessage.class));
        });

    }

    @Test
    void 비가_온다는_태그가_아닌_다른_태그_저장시_같은_지역의_사용자에게_알림이_전송되지_않는다() {
        String email = postMember.getEmail();
        CreatePost createPost = CreatePost.builder()
                .content("testContent")
                .dustTag(DustTag.NORMAL)
                .humidityTag(HumidityTag.PLEASANT)
                .windTag(WindTag.NONE)
                .temperatureTag(TemperatureTag.COMMON)
                .skyTag(SkyTag.CLOUDY)
                .build();

        postService.createWeatherPost(email, createPost);
        TestTransaction.flagForCommit();
        TestTransaction.end();

        await().atMost(5, SECONDS).untilAsserted(() -> {
            verify(alarmCooldownService, times(0)).setCooldown(AlarmType.RAIN_ALERT, savedAlarm.getFcmToken());
            verify(sender, times(0)).send(any(FcmMessage.class));
        });

    }

    @Test
    void 쿨다운_상태인_사용자에게는_알림이_전송되지_않는다() {
        alarmCooldownService.setCooldown(AlarmType.RAIN_ALERT, savedAlarm.getFcmToken());
        String email = postMember.getEmail();
        CreatePost createPost = CreatePost.builder()
                .content("testContent")
                .dustTag(DustTag.NORMAL)
                .humidityTag(HumidityTag.PLEASANT)
                .windTag(WindTag.NONE)
                .temperatureTag(TemperatureTag.COMMON)
                .skyTag(SkyTag.RAIN)
                .build();

        postService.createWeatherPost(email, createPost);
        TestTransaction.flagForCommit();
        TestTransaction.end();

        await().atMost(5, SECONDS).untilAsserted(() -> {
            verify(alarmCooldownService, times(1)).setCooldown(AlarmType.RAIN_ALERT, savedAlarm.getFcmToken());
            verify(sender, times(0)).send(any(FcmMessage.class));
        });
    }
}
