package org.pknu.weather.common;

import java.time.LocalDateTime;
import org.pknu.weather.domain.Location;
import org.pknu.weather.domain.Member;
import org.pknu.weather.domain.Post;
import org.pknu.weather.domain.common.PostType;
import org.pknu.weather.domain.common.Sensitivity;

public class TestDataCreator {
    private static long locationIdx = 1;
    private static long memberIdx = 1;

    public static Member getBusanMember() {
        return Member.builder()
                .location(getBusanLocation())
                .email("test@naver.com")
                .profileImage("http://test.png")
                .sensitivity(Sensitivity.HOT)
                .nickname("busan member")
                .build();
    }

    public static Member getBusanMember(Long memberId) {
        return Member.builder()
                .id(memberId)
                .location(getBusanLocation())
                .email("test@naver.com")
                .profileImage("http://test.png")
                .sensitivity(Sensitivity.HOT)
                .nickname("busan member")
                .build();
    }

    public static Member getBusanMember(String nickname) {
        return Member.builder()
                .location(getBusanLocation())
                .email(nickname + "@naver.com")
                .profileImage("http://test.png")
                .sensitivity(Sensitivity.HOT)
                .nickname(nickname)
                .build();
    }

    public static Post getPost(Member member) {
        return Post.builder()
                .location(member.getLocation())
                .member(member)
                .content("content")
                .postType(PostType.WEATHER)
                .build();
    }

    public static LocalDateTime getLocalDateTime() {
        return LocalDateTime.now()
                .withMinute(0)
                .withSecond(0)
                .withNano(0);
    }

    public static LocalDateTime getLocalDateTimePlusHours(int hours) {
        return getLocalDateTime().plusHours(hours);
    }

    public static Location getBusanLocation() {
        return Location.builder()
                .city("시군구")
                .province("부산광역시" + locationIdx++)
                .street("읍면동")
                .latitude(TestGlobalParams.BusanGeometry.LATITUDE)
                .longitude(TestGlobalParams.BusanGeometry.LONGITUDE)
                .build();
    }

    public static Location getSeoulLocation() {
        return Location.builder()
                .city("시군구")
                .province("서울광역시" + locationIdx++)
                .street("읍면동")
                .latitude(TestGlobalParams.SeoulGeometry.LATITUDE)
                .longitude(TestGlobalParams.SeoulGeometry.LONGITUDE)
                .build();
    }

}
