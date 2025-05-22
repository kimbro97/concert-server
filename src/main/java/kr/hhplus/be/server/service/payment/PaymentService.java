package kr.hhplus.be.server.service.payment;

import static kr.hhplus.be.server.support.exception.BusinessError.*;
import static kr.hhplus.be.server.support.lock.LockType.*;

import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.OptimisticLockException;
import kr.hhplus.be.server.domain.balance.Balance;
import kr.hhplus.be.server.domain.balance.BalanceRepository;
import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.payment.PaymentEventPublisher;
import kr.hhplus.be.server.domain.payment.PaymentRepository;
import kr.hhplus.be.server.domain.reservation.Reservation;
import kr.hhplus.be.server.domain.reservation.ReservationRepository;
import kr.hhplus.be.server.domain.token.TokenRepository;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import kr.hhplus.be.server.support.lock.DistributedLock;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentService {

	private final UserRepository userRepository;
	private final TokenRepository tokenRepository;
	private final PaymentRepository paymentRepository;
	private final BalanceRepository balanceRepository;
	private final PaymentEventPublisher eventPublisher;
	private final ReservationRepository reservationRepository;

	@Transactional
	@DistributedLock(key = "'userId:' + #command.getUserId()", leaseTime = 2, type = SIMPLE)
	public PaymentInfo pay(PaymentCommand command) {

		try {

			User user = userRepository.findById(command.getUserId())
				.orElseThrow(NOT_FOUND_USER_ERROR::exception);

			Reservation reservation = reservationRepository.findById(command.getReservationId())
				.orElseThrow(NOT_FOUND_RESERVATION_ERROR::exception);

			Balance balance = balanceRepository.findByUserId(command.getUserId())
				.orElseThrow(NOT_FOUND_BALANCE_ERROR::exception);

			Payment payment = Payment.create(user, reservation);
			payment.pay(balance, command.getNow());

			tokenRepository.deleteByUuid(command.getUuid());
			paymentRepository.save(payment);
			balanceRepository.saveAndFlush(balance);

			eventPublisher.paymentCompleted(payment);

			return PaymentInfo.from(payment);

		} catch (ObjectOptimisticLockingFailureException | OptimisticLockException e) {

			throw CONCURRENT_POINT_USE_CONFLICT.exception();

		}
	}
}
