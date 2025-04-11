package kr.hhplus.be.server.service.payment;

import static kr.hhplus.be.server.support.exception.BusinessError.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import kr.hhplus.be.server.domain.balance.Balance;
import kr.hhplus.be.server.domain.balance.BalanceRepository;
import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.payment.PaymentRepository;
import kr.hhplus.be.server.domain.payment.PaymentStatus;
import kr.hhplus.be.server.domain.reservation.Reservation;
import kr.hhplus.be.server.domain.reservation.ReservationRepository;
import kr.hhplus.be.server.domain.token.TokenRepository;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import kr.hhplus.be.server.support.exception.BusinessException;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {
	@Mock UserRepository userRepository;
	@Mock ReservationRepository reservationRepository;
	@Mock BalanceRepository balanceRepository;
	@Mock PaymentRepository paymentRepository;
	@Mock TokenRepository tokenRepository;

	@InjectMocks PaymentService paymentService;

	@Test
	@DisplayName("유저, 예약, 잔액이 유효한 경우 결제에 성공하고 PaymentInfo를 반환한다")
	void pay_성공() {
		// arrange
		Long userId = 1L;
		Long reservationId = 10L;
		Long totalAmount = 20000L;
		LocalDateTime now = LocalDateTime.now();

		PaymentCommand command = new PaymentCommand(userId, reservationId, "uuid_1");

		User user = new User("kim", "1234");
		Reservation reservation = mock(Reservation.class);
		Balance balance = mock(Balance.class);

		// 기본 정보
		when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));
		when(balanceRepository.findByUserId(userId)).thenReturn(Optional.of(balance));
		when(reservation.getTotalAmount()).thenReturn(totalAmount);
		when(reservation.getId()).thenReturn(reservationId);

		// 결제 생성
		Payment payment = Payment.create(user, reservation);
		payment.pay(now);

		// 저장 시 반환 객체 설정
		when(paymentRepository.save(any())).thenReturn(payment);

		// act
		PaymentInfo result = paymentService.pay(command);

		// assert
		assertThat(result).isNotNull();
		assertThat(result.getStatus()).isEqualTo(PaymentStatus.PAID.name());
		assertThat(result.getPaidAt()).isNotNull();

		verify(balance).use(totalAmount);
		verify(tokenRepository).deleteByUuid("uuid_1");
		verify(paymentRepository).save(any(Payment.class));
		verify(balanceRepository).save(balance);
	}


	@Test
	@DisplayName("존재하지 않는 유저 ID로 결제 요청 시 예외가 발생한다")
	void pay_유저없음() {
		// arrange
		Long userId = 1L;
		PaymentCommand command = new PaymentCommand(userId, 1L, "uuid_1");

		when(userRepository.findById(userId)).thenReturn(Optional.empty());

		// act & assert
		assertThatThrownBy(() -> paymentService.pay(command))
			.isInstanceOf(BusinessException.class);

		// 이후 로직이 실행되지 않았는지 확인
		verifyNoInteractions(reservationRepository, balanceRepository, paymentRepository);
	}

	@Test
	@DisplayName("존재하지 않는 예약 ID로 결제 요청 시 예외가 발생한다")
	void pay_예약없음() {
		// arrange
		Long userId = 1L;
		Long reservationId = 100L;
		PaymentCommand command = new PaymentCommand(userId, reservationId, "uuid_1");

		when(userRepository.findById(userId)).thenReturn(Optional.of(new User("kim", "1234")));
		when(reservationRepository.findById(reservationId)).thenReturn(Optional.empty());

		// act & assert
		assertThatThrownBy(() -> paymentService.pay(command))
			.isInstanceOf(BusinessException.class);

		verify(balanceRepository, never()).findByUserId(any());
		verify(paymentRepository, never()).save(any());
	}

	@Test
	@DisplayName("잔액 정보가 존재하지 않으면 예외가 발생하고 결제가 진행되지 않는다")
	void pay_잔액없음() {
		// arrange
		Long userId = 1L;
		Long reservationId = 100L;
		PaymentCommand command = new PaymentCommand(userId, reservationId, "uuid_1");

		User user = new User("kim", "1234");
		Reservation reservation = mock(Reservation.class);

		when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));
		when(balanceRepository.findByUserId(userId)).thenReturn(Optional.empty());

		// act & assert
		assertThatThrownBy(() -> paymentService.pay(command))
			.isInstanceOf(BusinessException.class);

		verify(paymentRepository, never()).save(any());
	}
}
