package kr.hhplus.be.server.service.concert;

import java.time.LocalDate;

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
}
