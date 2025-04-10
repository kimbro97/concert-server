package kr.hhplus.be.server.domain.token;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import kr.hhplus.be.server.domain.concert.Concert;
import kr.hhplus.be.server.domain.concert.Schedule;
import kr.hhplus.be.server.domain.user.User;

class TokenTest {
	@Test
	@DisplayName("Token.create 메서드로 토큰을 생성할 수 있다")
	void create_성공() {
		// arrange
		User user = new User("tester", "1234");
		Concert concert = new Concert("락페스티벌");
		Schedule schedule = new Schedule(concert, LocalDate.now());
		String uuid = "123e4567-e89b-12d3-a456-426614174000";
		TokenStatus status = TokenStatus.ACTIVE;

		// act
		Token token = Token.create(user, schedule, uuid, status);

		// assert
		assertThat(token.getUser()).isEqualTo(user);
		assertThat(token.getSchedule()).isEqualTo(schedule);
		assertThat(token.getUuid()).isEqualTo(uuid);
		assertThat(token.getStatus()).isEqualTo(status);
	}
}
