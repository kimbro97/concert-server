package kr.hhplus.be.server.service.payment;

import java.time.LocalDateTime;

import lombok.Getter;

@Getter
public class PaymentCommand {

	private Long userId;
	private Long reservationId;
	private String uuid;
	private LocalDateTime now;

	public PaymentCommand(Long userId, Long reservationId, String uuid, LocalDateTime now) {
		this.userId = userId;
		this.reservationId = reservationId;
		this.uuid = uuid;
		this.now = now;
	}
}
