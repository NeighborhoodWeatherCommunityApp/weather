package org.pknu.weather.apipayload.code.status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.pknu.weather.apipayload.code.BaseCode;
import org.pknu.weather.apipayload.code.ReasonDTO;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessStatus implements BaseCode {
    _OK(HttpStatus.OK, "200", "정상적인 요청."),
    _REDIRECT(HttpStatus.SEE_OTHER, "303", "다른 URL로 대체됩니다."),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ReasonDTO getReason() {
        return ReasonDTO.builder()
                .isSuccess(true)
                .message(message)
                .code(code)
                .build();
    }

    @Override
    public ReasonDTO getReasonHttpStatus() {
        return ReasonDTO.builder()
                .isSuccess(true)
                .message(message)
                .code(code)
                .httpStatus(httpStatus)
                .build();
    }
}
