package kr.hhplus.be.server.support.exception;

import static org.springframework.http.HttpStatus.*;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum BusinessError {

	// 유저 관련 Error
	USER_ID_HEADER_REQUIRED(BAD_REQUEST, "userId는 header에 필수 값 입니다."),
	NOT_FOUND_USER_ERROR(NOT_FOUND, "유저를 찾을 수 없습니다." ),

	// 필드 관련 Error
	CHARGE_AMOUNT_MUST_BE_POSITIVE(BAD_REQUEST, "충전 금액은 0보다 커야 합니다."),

	// 발란스 관련 Error
	NOT_FOUND_BALANCE_ERROR(NOT_FOUND, "발란스를 찾을 수 없습니다."),

	// 콘서트 관련 Error
	NOT_FOUND_CONCERT_ERROR(NOT_FOUND, "콘서트 항목을 찾을 수 없습니다." ),
	PAST_DATE_NOT_ALLOWED(BAD_REQUEST, "지난 날짜는 조회할 수 없습니다."),
	NOT_FOUND_SCHEDULE_ERROR(NOT_FOUND, "스케줄 항목을 찾을 수 없습니다." ),
	NOT_FOUND_SEAT_ERROR(NOT_FOUND, "좌석을 찾을 수 없습니다." ),

	// 예약 관련
	ALREADY_RESERVED_SEAT(BAD_REQUEST, "이미 예약된 좌석입니다."),
	NOT_FOUND_RESERVATION_ERROR(BAD_REQUEST, "예약 항목을 찾을 수 없습니다."),
	ALREADY_RESERVED_CANCEL_ERROR(BAD_REQUEST, "이미 취소된 좌석입니다."),

	// 결제
	EXPIRED_RESERVATION_ERROR(BAD_REQUEST, "예약시간이 만료되어 결제하실 수 없습니다."),
	ALREADY_PAID_ERROR(BAD_REQUEST, "이미 결제가 완료되었습니다."),
	NOT_ENOUGH_POINT_ERROR(BAD_REQUEST, "회원님의 잔액이 부족합니다."),

	// 토큰 관련
	NOT_FOUND_TOKEN_ERROR(BAD_REQUEST, "토큰값을 찾을 수 없습니다."),
	TOKEN_NOT_ACTIVE_ERROR(BAD_REQUEST, "토큰이 ACTIVE 상태가 아니므로 사용할 수 없습니다.");


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
