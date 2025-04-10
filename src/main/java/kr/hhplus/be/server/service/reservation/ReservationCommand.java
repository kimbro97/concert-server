package kr.hhplus.be.server.service.reservation;

import lombok.Getter;

@Getter
public class ReservationCommand {

	private Long userId;
	private Long scheduleId;
	private Long seatId;

	public ReservationCommand(Long userId, Long scheduleId, Long seatId) {
		this.userId = userId;
		this.scheduleId = scheduleId;
		this.seatId = seatId;
	}
}
