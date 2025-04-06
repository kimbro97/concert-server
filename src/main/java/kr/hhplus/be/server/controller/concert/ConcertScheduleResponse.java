package kr.hhplus.be.server.controller.concert;

import java.time.LocalDate;

public class ConcertScheduleResponse {
	private LocalDate date;

	public ConcertScheduleResponse(LocalDate date) {
		this.date = date;
	}

	public LocalDate getDate() {
		return date;
	}
}
