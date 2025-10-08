package org.pknu.weather.weather.feignclient.weatherapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class KmsApiHubResponseDTO {

    @JsonProperty("response")
    private Response response;

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Response {
        @JsonProperty("header")
        private Header header;

        @JsonProperty("body")
        private Body body;
    }

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
        }
    }
}
