package kr.hhplus.be.server.domain.token;

import static kr.hhplus.be.server.domain.token.TokenStatus.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
		TokenStatus status = ACTIVE;

		// act
		Token token = Token.create(user, schedule, uuid, status);

		// assert
		assertThat(token.getUser()).isEqualTo(user);
		assertThat(token.getSchedule()).isEqualTo(schedule);
		assertThat(token.getUuid()).isEqualTo(uuid);
		assertThat(token.getStatus()).isEqualTo(status);
	}

	@Test
	@DisplayName("토큰의 순번이 1이고 활성 토큰 수가 최대보다 작으면 상태를 ACTIVE로 변경한다")
	void activateIfFirstAndAvailable_조건충족() {
		// arrange
		User user = new User("kimbro", "1234");
		Schedule schedule = mock(Schedule.class);
		LocalDateTime now = LocalDateTime.now();
		Token token = Token.create(user, schedule, "uuid-1", PENDING);

		// act
		token.activate(1L, 500L, now.plusMinutes(10));

		// assert
		assertThat(token.getStatus()).isEqualTo(ACTIVE);
		assertThat(token.getExpireAt()).isEqualTo(now.plusMinutes(10));
	}

	@Test
	@DisplayName("토큰의 순번이 1이 아니면 상태를 변경하지 않는다")
	void activateIfFirstAndAvailable_순번1아님() {
		// arrange
		User user = new User("kimbro", "1234");
		Schedule schedule = mock(Schedule.class);
		LocalDateTime now = LocalDateTime.now();
		Token token = Token.create(user, schedule, "uuid-2", PENDING);

		// act
		token.activate(2L, 500L, now.plusMinutes(10));

		// assert
		assertThat(token.getStatus()).isEqualTo(PENDING);
	}

	@Test
	@DisplayName("활성 토큰 수가 최대치를 넘으면 상태를 변경하지 않는다")
	void activateIfFirstAndAvailable_최대초과() {
		// arrange
		User user = new User("kimbro", "1234");
		Schedule schedule = mock(Schedule.class);
		LocalDateTime now = LocalDateTime.now();
		Token token = Token.create(user, schedule, "uuid-3", PENDING);

		// act
		token.activate(1L, 1000L, now.plusMinutes(10));

		// assert
		assertThat(token.getStatus()).isEqualTo(PENDING);
	}

	@Test
	@DisplayName("PENDING 상태의 토큰에서 isActive를 호출하면 false를 리턴한다")
	void isActiveFalse() {
		User user = mock(User.class);
		Schedule schedule = mock(Schedule.class);
		String uuid = "uuid_1";
		Token token = Token.create(user, schedule, uuid, PENDING);
		boolean active = token.isActive();

		assertThat(active).isFalse();
	}

	@Test
	@DisplayName("ACTIVE 상태의 토큰에서 isActive를 호출하면 true를 리턴한다")
	void isActiveTrue() {
		User user = mock(User.class);
		Schedule schedule = mock(Schedule.class);
		String uuid = "uuid_1";
		Token token = Token.create(user, schedule, uuid, ACTIVE);
		boolean active = token.isActive();

		assertThat(active).isTrue();
	}
}
