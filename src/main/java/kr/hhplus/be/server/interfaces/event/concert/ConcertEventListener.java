package kr.hhplus.be.server.interfaces.event.concert;

import java.time.LocalDateTime;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import kr.hhplus.be.server.domain.payment.PaymentCompletedEvent;
import kr.hhplus.be.server.service.concert.ConcertCommand;
import kr.hhplus.be.server.service.concert.ConcertService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ConcertEventListener {

	private final ConcertService concertService;

	@KafkaListener(topics = "payment-completed", groupId = "concert-ranking-group")
	public void paymentUpdateRankingIfSoldOut(PaymentCompletedEvent event) {
		concertService.addRanking(
			ConcertCommand.AddRanking
				.builder()
				.concertId(event.getConcertId())
				.paymentId(event.getPaymentId())
				.scheduleId(event.getScheduleId())
				.today(LocalDateTime.now())
				.openedAt(event.getOpenedAt())
				.scheduleDate(event.getScheduleDate())
				.build()
		);
	}
}
