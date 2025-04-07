package kr.hhplus.be.server.support.advice;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import kr.hhplus.be.server.interfaces.common.ApiResponse;
import kr.hhplus.be.server.support.exception.BusinessException;

@RestControllerAdvice
public class ExceptionAdvice {

	@ExceptionHandler(BusinessException.class)
	public ApiResponse<Void> BusinessExceptionHandler(BusinessException e) {
		return ApiResponse.BusinessException(e.getHttpStatus(), e.getMessage());
	}
}
