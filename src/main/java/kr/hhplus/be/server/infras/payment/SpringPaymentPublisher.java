package kr.hhplus.be.server.infras.payment;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.payment.PaymentCompletedEvent;
import kr.hhplus.be.server.domain.payment.PaymentEventPublisher;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SpringPaymentPublisher implements PaymentEventPublisher {

	private final ApplicationEventPublisher eventPublisher;

	@Override
	public void paymentCompleted(Payment payment) {
		eventPublisher.publishEvent(PaymentCompletedEvent.of(payment));
	}

}
