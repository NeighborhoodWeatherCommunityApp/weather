package org.pknu.weather.location.feignClient.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SgisAccessTokenResponseDTO {

    private String id;
    private Result result;
    private String errMsg;
    private int errCd;

    @Getter
    @Setter
    public static class Result {
        private String accessToken;
        private long accessTimeout;
    }
}

