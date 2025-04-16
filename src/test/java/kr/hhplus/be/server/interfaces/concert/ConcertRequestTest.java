package kr.hhplus.be.server.interfaces.concert;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import kr.hhplus.be.server.interfaces.api.concert.ConcertRequest;
import kr.hhplus.be.server.service.concert.ConcertCommand;
import kr.hhplus.be.server.support.exception.BusinessException;

class ConcertRequestTest {
	@Test
	@DisplayName("정상적인 연월을 입력하면 ConcertCommand.Schedule이 생성된다")
	void toCommand_성공() {
		ConcertRequest.ConcertSchedule request = new ConcertRequest.ConcertSchedule("2025-04");

		ConcertCommand.Schedule command = request.toCommand(1L);
		assertThat(command.getConcertId()).isEqualTo(1L);
		assertThat(command.getStartData()).isEqualTo(LocalDate.now());
	}

	@Test
	@DisplayName("과거 연월이 입력되면 예외가 발생한다")
	void toCommand_과거날짜_예외() {

		YearMonth past = YearMonth.now().minusMonths(1);
		String pastDate = past.format(DateTimeFormatter.ofPattern("yyyy-MM"));
		ConcertRequest.ConcertSchedule request = new ConcertRequest.ConcertSchedule(pastDate);

		assertThatThrownBy(() -> request.toCommand(1L))
			.isInstanceOf(BusinessException.class);

	}
}
