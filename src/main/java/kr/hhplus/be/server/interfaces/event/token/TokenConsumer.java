package kr.hhplus.be.server.interfaces.event.token;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import kr.hhplus.be.server.domain.token.CreateTokenEvent;
import kr.hhplus.be.server.service.token.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenConsumer {

	private final TokenService tokenService;

	@KafkaListener(topics = "waiting-token", groupId = "token-activate")
	public void onTokenCreated(CreateTokenEvent event) {
		tokenService.activateToken(event.getScheduleId());
	}
}
