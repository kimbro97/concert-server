package kr.hhplus.be.server.interfaces.api.reservation;

import kr.hhplus.be.server.service.reservation.ReservationCommand;
import lombok.Getter;

@Getter
public class ReservationRequest {
	private Long userId;
	private Long scheduleId;
	private Long seatId;

	public ReservationRequest() {
	}

	public ReservationRequest(Long userId, Long scheduleId, Long seatId) {
		this.userId = userId;
		this.scheduleId = scheduleId;
		this.seatId = seatId;
	}

	public ReservationCommand toCommand() {
		return new ReservationCommand(userId, scheduleId, seatId);
	}
}
