package org.pknu.weather.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
// TODO: 낭비되는 공간이 많은데 개선 필요.. 이 형태 아니면 동작 X HttpMessageConverter을 직접 구현하는 것도...
public class WeatherApiResponse {

    @JsonProperty("response")
    private Response response;

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
