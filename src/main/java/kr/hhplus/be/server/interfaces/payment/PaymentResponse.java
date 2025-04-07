package kr.hhplus.be.server.interfaces.payment;

import java.time.LocalDateTime;

public class PaymentResponse {
	private final Long paymentId;
	private final Long reservationId;
	private final Long totalAmount;
	private final LocalDateTime createdAt;
	private final LocalDateTime updatedAt;

	public PaymentResponse(Long paymentId, Long reservationId, Long totalAmount, LocalDateTime createdAt,
		LocalDateTime updatedAt) {
		this.paymentId = paymentId;
		this.reservationId = reservationId;
		this.totalAmount = totalAmount;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	public Long getPaymentId() {
		return paymentId;
	}

	public Long getReservationId() {
		return reservationId;
	}

	public Long getTotalAmount() {
		return totalAmount;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}
}
