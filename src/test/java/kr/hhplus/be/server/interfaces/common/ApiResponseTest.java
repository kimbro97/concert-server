package kr.hhplus.be.server.interfaces.common;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class ApiResponseTest {

	@Test
	@DisplayName("message 없이 OK로 생성시")
	void not_message_ok() {
		ApiResponse<Object> response = ApiResponse.OK(null);

		assertThat(response.getMessage()).isEqualTo("요청이 성공적으로 처리되었습니다.");
		assertThat(response.getHttpStatus()).isEqualTo(HttpStatus.OK);
		assertThat(response.getData()).isEqualTo(null);
	}

	@Test
	@DisplayName("message가 있고 OK로 생성시")
	void message_ok() {
		ApiResponse<Object> response = ApiResponse.OK("요청이 처리됨", null);

		assertThat(response.getMessage()).isEqualTo("요청이 처리됨");
		assertThat(response.getHttpStatus()).isEqualTo(HttpStatus.OK);
		assertThat(response.getData()).isEqualTo(null);
	}

	@Test
	@DisplayName("message 없이 CREATE로 생성시")
	void not_message_create() {
		ApiResponse<Object> response = ApiResponse.CREATE(null);

		assertThat(response.getMessage()).isEqualTo("요청이 성공적으로 처리되었습니다.");
		assertThat(response.getHttpStatus()).isEqualTo(HttpStatus.CREATED);
		assertThat(response.getData()).isEqualTo(null);
	}

	@Test
	@DisplayName("message가 있고 CREATE로 생성시")
	void message_create() {
		ApiResponse<Object> response = ApiResponse.CREATE("데이터가 저장되었습니다.", null);

		assertThat(response.getMessage()).isEqualTo("데이터가 저장되었습니다.");
		assertThat(response.getHttpStatus()).isEqualTo(HttpStatus.CREATED);
		assertThat(response.getData()).isEqualTo(null);
	}

}
