package kr.hhplus.be.server.domain.concert;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import kr.hhplus.be.server.support.exception.BusinessException;

class ScheduleTest {

	@Test
	@DisplayName("openedAt이 현재 시간보다 이후라면 예외를 던진다")
	void opened_exception () {
	    // arrange
		Concert concert = new Concert("아이유 10주년 콘서트");
		Schedule schedule = new Schedule(concert, LocalDate.now(), LocalDateTime.now().plusMinutes(4));
		// act & assert
		assertThatThrownBy(() -> schedule.validateOpen(LocalDateTime.now()))
			.isInstanceOf(BusinessException.class);
	}

	@Test
	@DisplayName("openedAt이 현재 시간과 같거나 이전이라면 예외 없이 통과한다")
	void opened () {
		// arrange
		Concert concert = new Concert("아이유 10주년 콘서트");
		Schedule schedule = new Schedule(concert, LocalDate.now(), LocalDateTime.now());
		// act & assert
		assertThatNoException().isThrownBy(() -> schedule.validateOpen(LocalDateTime.now()));
	}
}
