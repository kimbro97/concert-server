package kr.hhplus.be.server.controller.reservation;

public class ReservationRequest {
	private final Long reservation_id;
	private final Long userId;
	private final Long concert_schedule_id;
	private final Long seat_id;

	public ReservationRequest(Long reservation_id, Long userId, Long concert_schedule_id, Long seat_id) {
		this.reservation_id = reservation_id;
		this.userId = userId;
		this.concert_schedule_id = concert_schedule_id;
		this.seat_id = seat_id;
	}

	public Long getReservation_id() {
		return reservation_id;
	}

	public Long getUserId() {
		return userId;
	}

	public Long getConcert_schedule_id() {
		return concert_schedule_id;
	}

	public Long getSeat_id() {
		return seat_id;
	}
}
