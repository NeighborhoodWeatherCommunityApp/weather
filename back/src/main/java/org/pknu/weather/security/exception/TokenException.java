package org.pknu.weather.security.exception;


import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletResponse;
import org.pknu.weather.apipayload.ApiResponse;
import org.pknu.weather.apipayload.code.BaseErrorCode;
import org.pknu.weather.apipayload.code.ErrorReasonDTO;
import org.pknu.weather.exception.GeneralException;
import org.springframework.http.MediaType;

import java.io.IOException;


public class TokenException extends GeneralException {

    public TokenException (BaseErrorCode errorCode) {
        super(errorCode);
    }


    public void sendResponseError(HttpServletResponse response){

        Gson gson = new Gson();

        try {
            ErrorReasonDTO errorReason = this.getErrorReasonHttpStatus();

            ApiResponse<Object> body = ApiResponse.onFailure(errorReason.getCode(),errorReason.getMessage(),null);

            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("utf-8");
            response.setStatus(errorReason.getHttpStatus().value());

            String responseStr = gson.toJson(body);

            response.getWriter().println(responseStr);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
