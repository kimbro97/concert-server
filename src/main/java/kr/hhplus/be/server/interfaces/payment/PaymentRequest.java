package kr.hhplus.be.server.interfaces.payment;

import kr.hhplus.be.server.service.payment.PaymentCommand;
import lombok.Getter;

@Getter
public class PaymentRequest {
	private final Long userId;
	private final Long reservationId;

	public PaymentRequest(Long userId, Long reservationId) {
		this.userId = userId;
		this.reservationId = reservationId;
	}

	public PaymentCommand toCommand() {
		return new PaymentCommand(userId, reservationId);
	}
}
