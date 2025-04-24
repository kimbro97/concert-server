package kr.hhplus.be.server.interfaces.api.payment;

import java.time.LocalDateTime;

import kr.hhplus.be.server.service.payment.PaymentCommand;
import lombok.Getter;

@Getter
public class PaymentRequest {
	private Long userId;
	private Long reservationId;

	public PaymentRequest() {
	}

	public PaymentRequest(Long userId, Long reservationId) {
		this.userId = userId;
		this.reservationId = reservationId;
	}

	public PaymentCommand toCommand(String uuid) {
		return new PaymentCommand(userId, reservationId, uuid, LocalDateTime.now());
	}
}
