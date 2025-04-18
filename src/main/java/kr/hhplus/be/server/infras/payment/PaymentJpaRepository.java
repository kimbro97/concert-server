package kr.hhplus.be.server.infras.payment;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.hhplus.be.server.domain.payment.Payment;

public interface PaymentJpaRepository extends JpaRepository<Payment, Long> {
}
