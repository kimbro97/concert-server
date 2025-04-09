package kr.hhplus.be.server.interfaces.reservation;

import java.time.LocalDateTime;

public class ReservationResponse {
	private final Long reservation_id;
	private final Long userId;
	private final Long concert_schedule_id;
	private final Long seat_id;
	private final Long totalAmount;
	private final String status;
	private final LocalDateTime createdAt;
	private final LocalDateTime updatedAt;

	public ReservationResponse(Long reservation_id, Long userId, Long concert_schedule_id, Long seat_id,
		Long totalAmount,
		String status, LocalDateTime createdAt, LocalDateTime updatedAt) {
		this.reservation_id = reservation_id;
		this.userId = userId;
		this.concert_schedule_id = concert_schedule_id;
		this.seat_id = seat_id;
		this.totalAmount = totalAmount;
		this.status = status;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
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

	public Long getTotalAmount() {
		return totalAmount;
	}

	public String getStatus() {
		return status;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}
}
