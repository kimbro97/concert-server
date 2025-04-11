package kr.hhplus.be.server.support.interceptor;

import static kr.hhplus.be.server.support.exception.BusinessError.*;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.hhplus.be.server.domain.token.Token;
import kr.hhplus.be.server.domain.token.TokenRepository;
import kr.hhplus.be.server.domain.token.TokenStatus;
import kr.hhplus.be.server.support.exception.BusinessError;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TokenInterceptor implements HandlerInterceptor {

	private final TokenRepository tokenRepository;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

		String uuid = request.getHeader("WAITING_TOKEN");

		Token token = tokenRepository.findByUuid(uuid).orElseThrow(NOT_FOUND_TOKEN_ERROR::exception);

		if (!token.getStatus().equals(TokenStatus.ACTIVE)) {
			throw TOKEN_NOT_ACTIVE_ERROR.exception();
		}

		return true;
	}
}
