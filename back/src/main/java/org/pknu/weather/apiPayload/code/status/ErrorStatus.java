package org.pknu.weather.apiPayload.code.status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.pknu.weather.apiPayload.code.BaseErrorCode;
import org.pknu.weather.apiPayload.code.ErrorReasonDTO;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {

    // 가장 일반적인 응답
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON_500", "서버 에러, 관리자에게 문의 바랍니다."),

    _BAD_REQUEST(HttpStatus.BAD_REQUEST,"COMMON_400","잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED,"COMMON_401","인증이 필요합니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON_403", "금지된 요청입니다."),

    // location
    _LOCATION_NOT_FOUND(HttpStatus.NOT_FOUND, "LOCATION_404_1", "존재하지 않는 지역입니다. 범위에 해당하는 위도와 경도 값을 입력하세요 "),

    // member
    _MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER_404_1", "사용자를 찾을 수 없습니다."),
    ;


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ErrorReasonDTO getReason() {
        return ErrorReasonDTO.builder()
                .isSuccess(false)
                .message(message)
                .code(code)
                .build();
    }

    @Override
    public ErrorReasonDTO getReasonHttpStatus() {
        return ErrorReasonDTO.builder()
                .isSuccess(false)
                .message(message)
                .code(code)
                .httpStatus(httpStatus)
                .build();
    }
}
