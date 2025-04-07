package kr.hhplus.be.server.support.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum BusinessError {

	// 유저관련 Error
	USER_ID_HEADER_REQUIRED(HttpStatus.BAD_REQUEST, "userId는 header에 필수 값 입니다.");


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
