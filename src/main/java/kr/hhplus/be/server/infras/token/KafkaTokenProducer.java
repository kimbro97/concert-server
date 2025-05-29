package kr.hhplus.be.server.infras.token;

import static org.springframework.transaction.event.TransactionPhase.*;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import kr.hhplus.be.server.domain.token.CreateTokenEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaTokenProducer {

	private final static String TOPIC = "waiting-token";
	private final KafkaTemplate<String, Object> kafkaTemplate;

	@Async
	@TransactionalEventListener(phase = AFTER_COMMIT)
	public void send(CreateTokenEvent createTokenEvent) {
		kafkaTemplate.send(TOPIC, createTokenEvent);
	}
}
