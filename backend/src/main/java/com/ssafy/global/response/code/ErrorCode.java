package com.ssafy.global.response.code;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode implements ResponseCode {

    /**
     * Common
     */
    INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "유효하지 않은 파라미터입니다."),

    UNAUTHORIZED_REQUEST(HttpStatus.UNAUTHORIZED, "인증되지 않은 사용자입니다."),

    FORBIDDEN_ACCESS(HttpStatus.FORBIDDEN, "권한이 존재하지 않는 사용자입니다."),

    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "리소스가 존재하지 않습니다."),

    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "허용되지 않은 METHOD 요청입니다."),

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버에서 오류가 발생했습니다."),

    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),

    /**
     * User
     */
    DUPLICATED_USER(HttpStatus.CONFLICT, "이미 가입된 유저 정보입니다."),
    DUPLICATED_EMAIL(HttpStatus.CONFLICT, "이미 해당 이메일로 가입된 정보가 존재합니다.."),

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "유저의 정보를 찾을 수 없습니다."),

    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "잘못된 패스워드입니다."),

    ACCOUNT_MISMATCH(HttpStatus.UNAUTHORIZED, "계정 정보가 일치하지 않습니다."),

    DUPLICATED_PASSWORD(HttpStatus.CONFLICT, "현재 패스워드와 같습니다."),

    UPDATE_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "이미 승인된 사용자입니다."),

    BLOCK_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "이미 정지된 사용자입니다."),

    SCRAPCOMPANY_NOT_FOUND(HttpStatus.NOT_FOUND, "기업스크랩 목록이 없습니다."),

    TENDENCY_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자성향 정보가 없습니다."),

    APPLY_TENDENCY_FAIL(HttpStatus.BAD_REQUEST, "사용자성향 정보 입력이 안됩니다."),

    /**
     * Company
     */
    COMPANY_NOT_FOUND(HttpStatus.NOT_FOUND, "기업검색 정보가 없습니다."),

    BASE_NOT_FOUND(HttpStatus.NOT_FOUND, "기업기본 정보가 없습니다."),

    SCRAP_NOT_FOUND(HttpStatus.NOT_FOUND, "관심기업 여부가 없습니다."),

    STATEMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "재무제표 정보가 없습니다."),

    RATE_NOT_FOUND(HttpStatus.NOT_FOUND, "재무비율 정보가 없습니다."),

    COMPANYYOUTH_NOT_FOUND(HttpStatus.NOT_FOUND, "청년친화기업 정보가 없습니다."),

    COMPANYQUARTERINFO_NOT_FOUND(HttpStatus.NOT_FOUND, "기업분기 정보가 없습니다."),

    COMPANYPOPULAR_NOT_FOUND(HttpStatus.NOT_FOUND, "조회수 top10 기업정보가 없습니다."),

    /**
     * News
     */
    NEWS_NOT_FOUND(HttpStatus.NOT_FOUND, "관련 최신 뉴스가 존재하지 않습니다."),

    /**
     * Patent
     */
    PATENT_NOT_FOUND(HttpStatus.NOT_FOUND, "특허 목록이 없습니다."),

    /**
     * Worldcup
     */
    WORLDCUP_NOT_FOUND(HttpStatus.NOT_FOUND, "가상기업 목록이 없습니다."),

    /**
     * Token
     */
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "토큰이 만료 되었습니다."),

    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    ;

    private final HttpStatus httpStatus;

    private final String message;

}
