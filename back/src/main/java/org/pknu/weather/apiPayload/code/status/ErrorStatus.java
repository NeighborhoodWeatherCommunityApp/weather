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

    _BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON_400_1", "잘못된 요청입니다."),
    _JSON_BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON_400_2", "요청 JSON의 형식이나 값이 잘못되었습니다."),
    _MUST_BE_POSITIVE(HttpStatus.BAD_REQUEST, "COMMON_400_3", "Id는 1이상이어야 합니다."),
    _BAD_REQUEST_DUPLICATED(HttpStatus.BAD_REQUEST, "COMMON_400_4", "중복 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON_401", "인증이 필요합니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON_403", "금지된 요청입니다."),

    // epx
    _EXP_NOT_NEGATIVE(HttpStatus.BAD_REQUEST, "EXP_400_1", "경험치는 음수일 수 없습니다."),
    _EXP_NOT_EXCEED(HttpStatus.BAD_REQUEST, "EXP_400_2", "경험치는 최대 경험치를 초과할 수 없습니다."),

    // tag
    _TAG_NOT_FOUND_FROM_CODE(HttpStatus.FORBIDDEN, "TAG_404_1", "요청하신 code로 태그를 찾을 수 없습니다."),
    _TAG_NOT_FOUND(HttpStatus.FORBIDDEN, "TAG_404_2", "해당 지역의 태그가 존재하지 않습니다."),

    //Token
    ACCESS_TOKEN_NOT_ACCEPTED(HttpStatus.UNAUTHORIZED, "Jwt_401_1", "Access Token이 존재하지 않습니다."),
    ACCESS_TOKEN_BADTYPE(HttpStatus.UNAUTHORIZED, "Jwt_401_2", "Access Token의 타입이 bearer가 아닙니다."),
    MALFORMED_ACCESS_TOKEN(HttpStatus.FORBIDDEN, "JWT_401_3", "Access Token의 값이 올바르게 설정되지 않았습니다. "),
    BAD_SIGNED_ACCESS_TOKEN(HttpStatus.FORBIDDEN, "JWT_401_4", "Access Token의 서명이 올바르지 않습니다."),
    EXPIRED_ACCESS_TOKEN(HttpStatus.FORBIDDEN, "JWT_401_5", "Access Token이 만료되었습니다."),
    MALFORMED_REFRESH_TOKEN(HttpStatus.FORBIDDEN, "JWT_401_7", "Refresh Token의 값이 올바르게 설정되지 않았습니다. "),
    EXPIRED_REFRESH_TOKEN(HttpStatus.FORBIDDEN, "JWT_401_8", "Refresh Token이 만료되었습니다."),
    TOKENS_NOT_ACCEPTED(HttpStatus.UNAUTHORIZED, "Jwt_401_9", "Access Token과 refresh Token이 존재하지 않습니다."),
    TYPE_NOT_ACCEPTED(HttpStatus.BAD_REQUEST, "Jwt_400_10", "소셜 로그인의 타입에 문제가 있습니다."),


    //KaKaoAccessTokenException
    KAKAO_SERVER_ERROR(HttpStatus.BAD_REQUEST, "Kakao_400_1", "카카오 서버의 일시적인 장애입니다."),
    KAKAO_ACCESS_TOKEN_BAT_TYPE(HttpStatus.BAD_REQUEST, "Kakao_400_2", "주어진 액세스 토큰 정보가 잘못된 형식입니다."),
    MALFORMED_KAKAO_ACCESS_TOKEN(HttpStatus.FORBIDDEN, "Kakao_401_3", "토큰 값이 유효하지 않습니다."),

    //appleTokenException
    MALFORMED_APPLE_TOKEN(HttpStatus.FORBIDDEN, "Apple_401_1", "잘못된 애플 identitiy Token입니다.."),


    // location
    _LOCATION_NOT_FOUND(HttpStatus.NOT_FOUND, "LOCATION_404_1", "존재하지 않는 지역입니다. 범위에 해당하는 위도와 경도 값을 입력하세요 "),
    _WEATHER_DATA_NOT_FOUND_IN_THE_LOCATION(HttpStatus.NOT_FOUND, "LOCATION_404_2", "해당 지역에 날씨 데이터가 없습니다."),
    _PROVINCE_NOT_FOUND(HttpStatus.BAD_REQUEST, "LOCATION_400_2", "도(광역시)의 정보가 필요합니다."),
    _MALFORMED_ADDRESS_INFORMATION(HttpStatus.BAD_REQUEST, "LOCATION_400_3", "주소의 일부 정보가 누락되었습니다."),

    // member
    _MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER_404_1", "사용자를 찾을 수 없습니다."),
    _MEMBER_NOT_FOUND_LOCATION(HttpStatus.NOT_FOUND, "MEMBER_404_2", "사용자의 기본 주소지가 설정되어 있지 않습니다."),
    _DUPILICATED_NICKNAME(HttpStatus.BAD_REQUEST, "MEMBER_400_3", "사용자의 닉네임이 중복됩니다."),

    // 약관동의
    _ESSENTIAL_TERMS_IS_REQUIRED(HttpStatus.BAD_REQUEST, "TERMS_400_1", "필수 약관 동의는 필수입니다."),

    //sigs
    _SGIS_BAD_AUTHENTICATION_PARAMETER(HttpStatus.UNAUTHORIZED, "SGIS_401_1",
            "sigs토큰을 얻기 위한 인증 정보가 잘못 설정되어 있습니다. 서버 관리자에게 문의 바랍니다."),
    _SGIS_NOT_FOUND_RESULT(HttpStatus.BAD_REQUEST, "SGIS_400_2", "sigs토큰을 검색 결과가 없습니다. 서버 관리자에게 문의 바랍니다."),

    // post
    _POST_NOT_FOUND(HttpStatus.NOT_FOUND, "POST_404_1", "게시글이 존재하지 않습니다."),
    _RECOMMENDATION_BAD_REQUEST(HttpStatus.BAD_REQUEST, "RECOMMENDATION_400_1", "이미 좋아요를 눌렀습니다."),
    _POST_CONTENT_OR_TAGS_REQUIRED(HttpStatus.BAD_REQUEST, "POST_400_2", "게시글의 내용이나 태그 중 하나는 필수입니다."),

    _POST_TYPE_NOT_FOUND(HttpStatus.BAD_REQUEST, "POST_404_2", "올바르지 않은 게시글 Type 입니다."),

    //feignClient
    _API_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "API_500_1", "외부 API 서버에 문제가 발생했습니다."),

    //database
    _DUPLICATED_ENTRY(HttpStatus.CONFLICT, "DATA_409_1", "중복된 데이터가 이미 존재합니다."),

    //alarm
    _FCMTOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "ALARM_404_1", "Fcm토큰을 찾을 수 없습니다."),
    _ALARM_NOT_FOUND(HttpStatus.NOT_FOUND, "ALARM_404_2", "알람을 찾을 수 없습니다.");


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
