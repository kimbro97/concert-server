package kr.hhplus.be.server.interfaces.event.reservation;

import static org.springframework.transaction.event.TransactionPhase.*;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import kr.hhplus.be.server.domain.payment.PaymentCompletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationEventListener {

	@Async
	@TransactionalEventListener(phase = AFTER_COMMIT)
	public void sendDataPlatform(PaymentCompletedEvent event) {
		try {
			log.info("데이터 플랫폼 서비스 로직 호출");
			Thread.sleep(2000);
			log.info("데이터 플랫폼 전송 성공");
		} catch (Exception e) {
			log.warn("데이터 플랫폼 예약정보 전달 실패 - paymentId: {}, message: {}", event.getPaymentId(), e.getMessage(), e);
		}
	}
}
