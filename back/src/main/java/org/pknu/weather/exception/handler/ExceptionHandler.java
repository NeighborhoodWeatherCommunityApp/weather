package org.pknu.weather.exception.handler;


import org.pknu.weather.apipayload.code.BaseErrorCode;
import org.pknu.weather.exception.GeneralException;

public class ExceptionHandler extends GeneralException {

    public ExceptionHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}