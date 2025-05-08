package kr.hhplus.be.server.interfaces.api.concert;

import static kr.hhplus.be.server.support.exception.BusinessError.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

import kr.hhplus.be.server.service.concert.ConcertCommand;
import lombok.Getter;

@Getter
public class ConcertRequest {

	@Getter
	public static class ConcertSchedule {

		private String date;

		public ConcertSchedule(String date) {
			this.date = date;
		}

		public ConcertCommand.Schedule toCommand(Long concertId) {
			this.validate();
			LocalDate startDate = LocalDate.now();

			YearMonth ym = YearMonth.parse(date, DateTimeFormatter.ofPattern("yyyy-MM"));
			LocalDate endDate = ym.atEndOfMonth();

			return new ConcertCommand.Schedule(concertId, startDate, endDate);
		}

		private void validate() {
			YearMonth input = YearMonth.parse(date, DateTimeFormatter.ofPattern("yyyy-MM"));
			YearMonth now = YearMonth.now();

			if (input.isBefore(now)) {
				throw PAST_DATE_NOT_ALLOWED.exception();
			}

		}
	}

}
