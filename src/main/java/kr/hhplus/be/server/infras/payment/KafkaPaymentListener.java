package kr.hhplus.be.server.infras.payment;

import static org.springframework.transaction.event.TransactionPhase.*;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import kr.hhplus.be.server.domain.payment.PaymentCompletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaPaymentListener {

	private final KafkaTemplate<String, Object> kafkaTemplate;

	@Async
	@TransactionalEventListener(phase = AFTER_COMMIT)
	public void send(PaymentCompletedEvent paymentCompletedEvent) {
		kafkaTemplate.send("payment-completed", paymentCompletedEvent);
	}
}
