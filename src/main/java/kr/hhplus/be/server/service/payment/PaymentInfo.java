package kr.hhplus.be.server.service.payment;

import java.time.LocalDateTime;

import kr.hhplus.be.server.domain.payment.Payment;
import lombok.Builder;
import lombok.Getter;

@Getter
public class PaymentInfo {

	private Long paymentId;
	private Long userId;
	private Long reservationId;
	private String status;
	private LocalDateTime paidAt;

	@Builder
	private PaymentInfo(Long paymentId, Long userId, Long reservationId, String status, LocalDateTime paidAt) {
		this.paymentId = paymentId;
		this.userId = userId;
		this.reservationId = reservationId;
		this.status = status;
		this.paidAt = paidAt;
	}

	public static PaymentInfo from(Payment payment) {
		return PaymentInfo.builder()
			.paymentId(payment.getId())
			.userId(payment.getUser().getId())
			.reservationId(payment.getReservation().getId())
			.status(payment.getStatus().name())
			.paidAt(payment.getPaidAt())
			.build();
	}
}
