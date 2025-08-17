package org.pknu.weather.security.exception;


import lombok.Getter;
import org.pknu.weather.apipayload.code.BaseErrorCode;
import org.pknu.weather.exception.GeneralException;

@Getter
public class WeatherFeignClientException extends GeneralException {
    private final String resultCode;  // 추가
    private final String resultMsg;   // 추가

    public WeatherFeignClientException(BaseErrorCode errorCode) {
        super(errorCode);
        resultCode = null;
        resultMsg = null;
    }

    public WeatherFeignClientException(BaseErrorCode errorCode, String resultCode, String resultMsg) {
        super(errorCode);
        this.resultCode = resultCode;
        this.resultMsg = resultMsg;
    }
}
