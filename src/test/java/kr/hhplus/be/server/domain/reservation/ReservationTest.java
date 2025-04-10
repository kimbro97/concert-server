package kr.hhplus.be.server.domain.reservation;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import kr.hhplus.be.server.domain.concert.Schedule;
import kr.hhplus.be.server.domain.concert.Seat;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.support.exception.BusinessException;

class ReservationTest {
	@Test
	@DisplayName("정상적으로 좌석을 예약하면 상태와 금액, 만료 시간이 설정된다")
	void reserve_성공() {
		// arrange
		User user = mock(User.class);
		Schedule schedule = mock(Schedule.class);
		Seat seat = mock(Seat.class);

		when(seat.calculatePrice()).thenReturn(12000L);

		Reservation reservation = Reservation.create(user, schedule, seat);

		// act
		reservation.reserve();

		// assert
		assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.RESERVED);
		assertThat(reservation.getTotalAmount()).isEqualTo(12000L);
		assertThat(reservation.getExpiredAt()).isNotNull();
		assertThat(reservation.getExpiredAt()).isAfter(LocalDateTime.now().minusSeconds(1));

		verify(seat, times(1)).reserve();
		verify(seat, times(1)).calculatePrice();
	}

	@Test
	@DisplayName("좌석 예약이 불가능하면 예외가 발생하고 이후 로직은 실행되지 않는다")
	void reserve_실패() {
		// arrange
		User user = mock(User.class);
		Schedule schedule = mock(Schedule.class);
		Seat seat = mock(Seat.class);

		doThrow(new BusinessException(null, "이미 예약된 좌석입니다."))
			.when(seat).reserve();

		Reservation reservation = Reservation.create(user, schedule, seat);

		// act & assert
		assertThatThrownBy(reservation::reserve)
			.isInstanceOf(BusinessException.class)
			.hasMessageContaining("이미 예약된 좌석");

		verify(seat, times(1)).reserve();
		verify(seat, times(0)).calculatePrice();
		assertThat(reservation.getStatus()).isNull();
		assertThat(reservation.getTotalAmount()).isNull();
		assertThat(reservation.getExpiredAt()).isNull();
	}
}
