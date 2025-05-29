package kr.hhplus.be.server.infras.token;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import kr.hhplus.be.server.domain.token.CreateTokenEvent;
import kr.hhplus.be.server.domain.token.Token;
import kr.hhplus.be.server.domain.token.TokenEventPublisher;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SpringTokenPublisher implements TokenEventPublisher {

	private final ApplicationEventPublisher eventPublisher;

	@Override
	public void createToken(Token token) {
		eventPublisher.publishEvent(CreateTokenEvent.of(token));
	}
}
