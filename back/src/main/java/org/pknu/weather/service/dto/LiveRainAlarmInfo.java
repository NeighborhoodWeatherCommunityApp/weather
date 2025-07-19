package org.pknu.weather.service.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LiveRainAlarmInfo implements AlarmInfo{
    private String fcmToken;
    private String province;
    private String city;
    private String street;
    private String postContent;

    public String getFullAddress() {
        return this.province + " " + this.city + " " + this.street;
    }
}
