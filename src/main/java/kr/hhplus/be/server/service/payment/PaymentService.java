package kr.hhplus.be.server.service.payment;

import static kr.hhplus.be.server.support.exception.BusinessError.*;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.hhplus.be.server.domain.balance.Balance;
import kr.hhplus.be.server.domain.balance.BalanceRepository;
import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.payment.PaymentRepository;
import kr.hhplus.be.server.domain.reservation.Reservation;
import kr.hhplus.be.server.domain.reservation.ReservationRepository;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentService {

	private final UserRepository userRepository;
	private final PaymentRepository paymentRepository;
	private final BalanceRepository balanceRepository;
	private final ReservationRepository reservationRepository;

	@Transactional
	public PaymentInfo pay(PaymentCommand command) {

		User user = userRepository.findById(command.getUserId())
			.orElseThrow(NOT_FOUND_USER_ERROR::exception);

		Reservation reservation = reservationRepository.findById(command.getReservationId())
			.orElseThrow(NOT_FOUND_RESERVATION_ERROR::exception);

		Balance balance = balanceRepository.findByUserId(command.getUserId())
			.orElseThrow(NOT_FOUND_BALANCE_ERROR::exception);

		balance.use(reservation.getTotalAmount());

		Payment payment = Payment.create(user, reservation);
		payment.pay(LocalDateTime.now());

		paymentRepository.save(payment);
		balanceRepository.save(balance);

		return PaymentInfo.from(payment);
	}
}
