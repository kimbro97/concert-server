package kr.hhplus.be.server.service.payment;

import lombok.Getter;

@Getter
public class PaymentCommand {

	private Long userId;
	private Long reservationId;
	private String uuid;

	public PaymentCommand(Long userId, Long reservationId, String uuid) {
		this.userId = userId;
		this.reservationId = reservationId;
		this.uuid = uuid;
	}
}
