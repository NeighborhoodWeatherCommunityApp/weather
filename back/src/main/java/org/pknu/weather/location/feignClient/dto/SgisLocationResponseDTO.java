package org.pknu.weather.location.feignClient.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@ToString
public class SgisLocationResponseDTO {

    private String id;
    private List<Result> result;
    private String errMsg;
    private int errCd;

    @Getter
    @Setter
    @ToString
    public static class Result {
        private String sido_nm;
        private String sgg_nm;
        private String emdong_nm;
    }
}

