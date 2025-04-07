package kr.hhplus.be.server.support.interceptor;

import static org.assertj.core.api.Assertions.*;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import kr.hhplus.be.server.support.exception.BusinessException;

class LoginInterceptorTest {

	LoginInterceptor loginInterceptor = new LoginInterceptor();

	@Test
	@DisplayName("requst header에 USER_ID 값이 있으면 true를 반환한다")
	void user_id_true() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();

		request.addHeader("USER_ID", "1234");

		boolean preHandle = loginInterceptor.preHandle(request, response, new Object());
		assertThat(preHandle).isTrue();
	}

	@Test
	@DisplayName("requst header에 USER_ID 값이 없으면 예외가 발생한다")
	void user_id_exception() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();

		assertThatThrownBy(() -> loginInterceptor.preHandle(request, response, new Object()))
			.isInstanceOf(BusinessException.class);
	}
}
