package kr.hhplus.be.server.domain.token;

import kr.hhplus.be.server.domain.payment.Payment;

public interface TokenEventPublisher {
	void createToken(Token Token);
}
