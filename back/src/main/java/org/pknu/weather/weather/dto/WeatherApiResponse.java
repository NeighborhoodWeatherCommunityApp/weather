package org.pknu.weather.weather.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherApiResponse {

    @JsonProperty("header")
    private Header header;

    @JsonProperty("response")
    private Response response;

    @Getter
    @NoArgsConstructor
    public static class Header {
        @JsonProperty("resultCode")
        private Integer resultCode;

        @JsonProperty("resultMsg")
        private String resultMsg;
    }

    @Getter
    @NoArgsConstructor
    public static class Response {

        @JsonProperty("body")
        private Body body;

        @Getter
        @NoArgsConstructor
        public static class Body {

            @JsonProperty("dataType")
            private String dataType;

            @JsonProperty("items")
            private Items items;

            @JsonProperty("pageNo")
            private int pageNo;

            @JsonProperty("numOfRows")
            private int numOfRows;

            @JsonProperty("totalCount")
            private int totalCount;

            @Getter
            @NoArgsConstructor
            public static class Items {

                @JsonProperty("item")
                private List<Item> itemList;

                @Getter
                @NoArgsConstructor
                public static class Item {

                    @JsonProperty("baseDate")
                    private String baseDate;

                    @JsonProperty("baseTime")
                    private String baseTime;

                    @JsonProperty("category")
                    private String category;

                    @JsonProperty("fcstDate")
                    private String fcstDate;

                    @JsonProperty("fcstTime")
                    private String fcstTime;

                    @JsonProperty("fcstValue")
                    private String fcstValue;

                    @JsonProperty("nx")
                    private int nx;

                    @JsonProperty("ny")
                    private int ny;
                }
            }
        }
    }
}
