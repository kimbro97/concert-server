package kr.hhplus.be.server.interfaces.event.concert;

import static org.springframework.transaction.event.TransactionPhase.*;

import java.time.LocalDateTime;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import kr.hhplus.be.server.domain.payment.PaymentCompletedEvent;
import kr.hhplus.be.server.service.concert.ConcertCommand;
import kr.hhplus.be.server.service.concert.ConcertService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ConcertEventListener {

	private final ConcertService concertService;

	@Async
	@TransactionalEventListener(phase = AFTER_COMMIT)
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
