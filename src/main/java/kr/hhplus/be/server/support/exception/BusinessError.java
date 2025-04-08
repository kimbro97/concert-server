package kr.hhplus.be.server.support.exception;

import static org.springframework.http.HttpStatus.*;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum BusinessError {

	// 유저 관련 Error
	USER_ID_HEADER_REQUIRED(BAD_REQUEST, "userId는 header에 필수 값 입니다."),

	// 필드 관련 Error
	CHARGE_AMOUNT_MUST_BE_POSITIVE(BAD_REQUEST, "충전 금액은 0보다 커야 합니다."),

	// 발란스 관련 Error
	NOT_FOUND_BALANCE_ERROR(NOT_FOUND, "발란스를 찾을 수 없습니다.")
	;


	private final HttpStatus httpStatus;
	private final String message;

	BusinessError(HttpStatus httpStatus, String message) {
		this.httpStatus = httpStatus;
		this.message = message;
	}

	public BusinessException exception() {
		return new BusinessException(httpStatus, message);
	}
}
