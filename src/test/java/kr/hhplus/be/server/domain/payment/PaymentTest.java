package kr.hhplus.be.server.domain.payment;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import kr.hhplus.be.server.domain.reservation.Reservation;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.support.exception.BusinessException;

class PaymentTest {
	@Test
	@DisplayName("만료되지 않은 예약에 대해 결제에 성공하면 상태와 시간 정보가 설정된다")
	void pay_성공() {
		// arrange
		User user = new User("test", "1234");
		Reservation reservation = mock(Reservation.class);
		Payment payment = Payment.create(user, reservation);
		LocalDateTime now = LocalDateTime.now();

		// act
		payment.pay(now);

		// assert
		assertThat(payment.getStatus()).isEqualTo(PaymentStatus.PAID);
		assertThat(payment.getPaidAt()).isEqualTo(now);

		verify(reservation).validateNotExpired(now);
	}

	@Test
	@DisplayName("만료된 예약이면 결제 시 예외가 발생하고 상태가 설정되지 않는다")
	void pay_예외() {
		// arrange
		User user = new User("test", "1234");
		Reservation reservation = mock(Reservation.class);
		Payment payment = Payment.create(user, reservation);
		LocalDateTime now = LocalDateTime.now();

		doThrow(new BusinessException(null, "예약이 만료되었습니다"))
			.when(reservation).validateNotExpired(now);

		// act & assert
		assertThatThrownBy(() -> payment.pay(now))
			.isInstanceOf(BusinessException.class)
			.hasMessageContaining("예약이 만료되었습니다");

		assertThat(payment.getStatus()).isNull();
		assertThat(payment.getPaidAt()).isNull();
	}
}
