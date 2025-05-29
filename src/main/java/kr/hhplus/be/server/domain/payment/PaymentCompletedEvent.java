package kr.hhplus.be.server.domain.payment;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
public class PaymentCompletedEvent {

	private Long paymentId;
	private Long concertId;
	private Long scheduleId;
	private LocalDate scheduleDate;
	private LocalDateTime openedAt;

	@Builder
	private PaymentCompletedEvent(Long paymentId, Long concertId, Long scheduleId, LocalDate scheduleDate, LocalDateTime openedAt) {
		this.paymentId = paymentId;
		this.concertId = concertId;
		this.scheduleId = scheduleId;
		this.scheduleDate = scheduleDate;
		this.openedAt = openedAt;
	}

	public static PaymentCompletedEvent of(Payment payment) {
		return PaymentCompletedEvent.builder()
			.paymentId(payment.getId())
			.concertId(payment.getReservation().getSchedule().getConcert().getId())
			.scheduleId(payment.getReservation().getSchedule().getId())
			.scheduleDate(payment.getReservation().getSchedule().getDate())
			.openedAt(payment.getReservation().getSchedule().getOpenedAt())
			.build();
	}
}
