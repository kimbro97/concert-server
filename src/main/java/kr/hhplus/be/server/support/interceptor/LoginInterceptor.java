package kr.hhplus.be.server.support.interceptor;

import static kr.hhplus.be.server.support.exception.BusinessError.*;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class LoginInterceptor implements HandlerInterceptor {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

		String userId = request.getHeader("USER_ID");

		if (userId == null) {
			throw USER_ID_HEADER_REQUIRED.exception();
		}

		return true;
	}
}
