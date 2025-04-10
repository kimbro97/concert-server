package kr.hhplus.be.server.service.concert;

import java.time.LocalDate;

import kr.hhplus.be.server.domain.concert.Schedule;
import kr.hhplus.be.server.domain.concert.Seat;
import lombok.Builder;
import lombok.Getter;

public class ConcertInfo {

	@Getter
	public static class ScheduleInfo {

		private final Long scheduleId;
		private final LocalDate date;

		@Builder
		public ScheduleInfo(Long scheduleId, LocalDate date) {
			this.scheduleId = scheduleId;
			this.date = date;
		}

		public static ConcertInfo.ScheduleInfo from(Schedule schedule) {
			return ConcertInfo.ScheduleInfo.builder()
				.scheduleId(schedule.getId())
				.date(schedule.getDate())
				.build();
		}
	}

	@Getter
	public static class SeatInfo {

		private final Long seatId;
		private final String number;
		private final Long price;
		private final Boolean isSelectable;

		@Builder
		public SeatInfo(Long seatId, String number, Long price, Boolean isSelectable) {
			this.seatId = seatId;
			this.number = number;
			this.price = price;
			this.isSelectable = isSelectable;
		}

		public static ConcertInfo.SeatInfo from(Seat seat) {
			return SeatInfo.builder()
				.seatId(seat.getId())
				.number(seat.getNumber())
				.price(seat.getPrice())
				.isSelectable(seat.getIsSelectable())
				.build();
		}
	}
}
