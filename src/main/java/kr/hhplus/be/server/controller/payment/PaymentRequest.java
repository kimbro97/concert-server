package kr.hhplus.be.server.controller.payment;

public class PaymentRequest {
	private final Long reservationId;

	public PaymentRequest(Long reservationId) {
		this.reservationId = reservationId;
	}

	public Long getReservationId() {
		return reservationId;
	}
}
