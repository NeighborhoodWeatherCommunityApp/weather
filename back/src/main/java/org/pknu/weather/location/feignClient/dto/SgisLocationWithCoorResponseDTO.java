package org.pknu.weather.location.feignClient.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SgisLocationWithCoorResponseDTO {

    private String id;
    private Result result;
    private String errMsg;
    private int errCd;

    @Getter
    @Setter
    public static class Result {
        private double x ;
        private double y;
        private List<ResultData> resultdata;
    }

    @Getter
    @Setter
    public static class ResultData {
        private double x ;
        private double y;
    }
}

