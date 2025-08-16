package org.pknu.weather.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.pknu.weather.apipayload.code.BaseErrorCode;
import org.pknu.weather.apipayload.code.ErrorReasonDTO;

@Getter
@AllArgsConstructor
public class GeneralException extends RuntimeException {

    private BaseErrorCode code;

    public ErrorReasonDTO getErrorReason() {
        return this.code.getReason();
    }

    public ErrorReasonDTO getErrorReasonHttpStatus(){
        return this.code.getReasonHttpStatus();
    }
}