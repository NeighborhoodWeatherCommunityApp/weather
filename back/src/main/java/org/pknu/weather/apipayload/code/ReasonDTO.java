package org.pknu.weather.apipayload.code;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@Builder
public class ReasonDTO {

    private final boolean isSuccess;
    private final String code;
    private final String message;
    private final HttpStatus httpStatus;
    public boolean getIsSuccess(){
        return isSuccess;
    }
}
