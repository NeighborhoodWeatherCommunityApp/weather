package org.pknu.weather.weather.feignClient.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class UVResponseDTO {

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
        private String dataType;
        private Items items;
        private int pageNo;
        private int numOfRows;
        private int totalCount;
    }

    @Getter
    @Setter
    @ToString
    public static class Items {
        private List<Item> item;
    }

    @Getter
    @Setter
    @ToString
    public static class Item {
        private String code;
        private long areaNo;
        private long date;
        private Integer h0;
        private Integer h3;
        private Integer h6;
        private Integer h9;
        private Integer h12;
        private Integer h15;
        private Integer h18;
        private Integer h21;
        private Integer h24;
        private Integer h27;
        private Integer h30;
        private Integer h33;
        private Integer h36;
        private Integer h39;
        private Integer h42;
        private Integer h45;
        private Integer h48;
        private Integer h51;
        private Integer h54;
        private Integer h57;
        private Integer h60;
        private Integer h63;
        private Integer h66;
        private Integer h69;
        private Integer h72;
        private Integer h75;
    }
}
