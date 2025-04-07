package kr.hhplus.be.server.interfaces.common;

import static org.springframework.http.HttpStatus.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import lombok.Getter;

@Getter
public class ApiResponse <T> {

	public static final String DEFAULT_MESSAGE = "요청이 성공적으로 처리되었습니다.";

	private final HttpStatus httpStatus;
	private final int httpStatusCode;
	private final String message;
	private final T data;

	public ApiResponse(HttpStatus httpStatus, int httpStatusCode, String message, T data) {
		this.httpStatus = httpStatus;
		this.httpStatusCode = httpStatusCode;
		this.message = message;
		this.data = data;
	}

	public static <T> ApiResponse<T> OK(T data) {
		return new ApiResponse<>(OK, OK.value(), DEFAULT_MESSAGE, data);
	}

	public static <T> ApiResponse<T> OK(String message, T data) {
		return new ApiResponse<>(OK, OK.value(), message, data);
	}

	public static <T> ApiResponse<T> CREATE(T data) {
		return new ApiResponse<>(CREATED, CREATED.value(),  DEFAULT_MESSAGE, data);
	}

	public static <T> ApiResponse<T> CREATE(String message, T data) {
		return new ApiResponse<>(CREATED, CREATED.value(), message, data);
	}

	public ResponseEntity<ApiResponse<T>> toResponseEntity() {
		return ResponseEntity.status(httpStatus).body(this);
	}
}
