package kr.hhplus.be.server.interfaces.api.reservation;

import kr.hhplus.be.server.service.reservation.ReservationCommand;
import lombok.Getter;

@Getter
public class ReservationRequest {
	private final Long userId;
	private final Long scheduleId;
	private final Long seatId;

	public ReservationRequest(Long userId, Long scheduleId, Long seatId) {
		this.userId = userId;
		this.scheduleId = scheduleId;
		this.seatId = seatId;
	}

	public ReservationCommand toCommand() {
		return new ReservationCommand(userId, scheduleId, seatId);
	}
}
