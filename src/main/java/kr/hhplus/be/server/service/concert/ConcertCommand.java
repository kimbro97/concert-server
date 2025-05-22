package kr.hhplus.be.server.service.concert;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

public class ConcertCommand {

	@Getter
	public static class Schedule {

		private final Long concertId;
		private final LocalDate startData;
		private final LocalDate endData;

		public Schedule(Long concertId, LocalDate startData, LocalDate endData) {
			this.concertId = concertId;
			this.startData = startData;
			this.endData = endData;
		}

	}

	@Getter
	public static class Seat {

		private Long scheduleId;

		public Seat(Long scheduleId) {
			this.scheduleId = scheduleId;
		}
	}

	@Getter
	public static class AddRanking {
		private Long paymentId;
		private Long concertId;
		private Long scheduleId;
		private LocalDateTime today;
		private LocalDate scheduleDate;
		private LocalDateTime openedAt;

		@Builder
		private AddRanking(Long paymentId, Long concertId, Long scheduleId, LocalDateTime today, LocalDate scheduleDate, LocalDateTime openedAt) {
			this.paymentId = paymentId;
			this.concertId = concertId;
			this.scheduleId = scheduleId;
			this.today = today;
			this.scheduleDate = scheduleDate;
			this.openedAt = openedAt;
		}
	}
}
