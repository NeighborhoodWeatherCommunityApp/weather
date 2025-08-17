package org.pknu.weather.weather.feignclient.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
public class AirConditionResponseDTO {

    private Response response;

    @Getter
    @Setter
    @ToString
    public static class Response {
        private Header header;
        private Body body;

    }

    @Getter
    @Setter
    @ToString
    public static class Header {
        private int resultCode;
        private String resultMsg;
    }

    @Getter
    @Setter
    @ToString
    public static class Body {
        private List<Item> items;
        private int pageNo;
        private int numOfRows;
        private int totalCount;
    }

    @Getter
    @Setter
    @ToString
    public static class Item {

        private Integer pm25Grade1h;
        private Integer pm10Grade1h;
        private Integer o3Grade;
        private Integer khaiGrade;
        private Integer so2Grade;
        private Integer no2Grade;
        private Integer pm10Grade;
        private Integer pm25Grade;
        private Integer coGrade;
        private String pm10Value;
        private String pm25Value;



        private String pm25Flag;
        private String no2Flag;
        private String coFlag;
        private String pm10Flag;
        private String o3Flag;
        private String so2Flag;

        private String pm10Value24;
        private String pm25Value24;
        private String khaiValue;
        private String so2Value;
        private String no2Value;
        private String coValue;
        private String o3Value;

        private String mangName;
        private String stationName;
        private Integer stationCode;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
        private LocalDateTime dataTime;
    }
}
