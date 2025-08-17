package org.pknu.weather.weather.feignclient.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class AirObservatoryResponseDTO {

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
        private String resultCode;
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
        private String stationCode;
        private long tm;
        private String addr;
        private String stationName;
    }
}
