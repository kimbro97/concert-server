package kr.hhplus.be.server.domain.reservation;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import kr.hhplus.be.server.domain.concert.Concert;
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
		reservation.reserve(LocalDateTime.now().plusMinutes(5), LocalDateTime.now());

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
		assertThatThrownBy(() -> reservation.reserve(LocalDateTime.now().plusMinutes(5), LocalDateTime.now()))
			.isInstanceOf(BusinessException.class)
			.hasMessageContaining("이미 예약된 좌석");

		verify(seat, times(1)).reserve();
		verify(seat, times(0)).calculatePrice();
		assertThat(reservation.getStatus()).isNull();
		assertThat(reservation.getTotalAmount()).isNull();
		assertThat(reservation.getExpiredAt()).isNull();
	}
	@Test
	@DisplayName("예약이 만료되지 않았다면 예외가 발생하지 않는다")
	void validateNotExpired_정상() {
		User user = mock(User.class);
		Schedule schedule = mock(Schedule.class);
		Seat seat = mock(Seat.class);

		Reservation reservation = Reservation.create(user, schedule, seat);
		reservation.reserve(LocalDateTime.now().plusMinutes(5), LocalDateTime.now());

		reservation.validateNotExpired(LocalDateTime.now());
	}

	@Test
	@DisplayName("예약이 만료되었다면 예외가 발생하고 이후 로직은 실행되지 않는다")
	void validateNotExpired_예외() {
		User user = mock(User.class);
		Schedule schedule = mock(Schedule.class);
		Seat seat = mock(Seat.class);

		Reservation reservation = Reservation.create(user, schedule, seat);
		reservation.reserve(LocalDateTime.now().minusMinutes(5), LocalDateTime.now());

		assertThatThrownBy(() -> reservation.validateNotExpired(LocalDateTime.now()))
			.isInstanceOf(BusinessException.class);
	}

	@Test
	@DisplayName("예약을 취소하면 상태가 CANCEL로 변경되고 좌석도 취소 처리된다")
	void cancel_성공() {
		// arrange
		User user = new User("testUser", "1234");
		Schedule schedule = mock(Schedule.class);
		Seat seat = mock(Seat.class);

		Reservation reservation = Reservation.create(user, schedule, seat);
		reservation.reserve(LocalDateTime.now().plusMinutes(5), LocalDateTime.now()); // RESERVED 상태로 만들어줌

		// act
		reservation.cancel();

		// assert
		assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.CANCEL);
		verify(seat, times(1)).cancel(); // seat.cancel() 이 호출되었는지 검증
	}

	@Test
	@DisplayName("예약취소시 예약상태가 CANCEL이라면 예외가 발생한다.")
	void cancel_already_exception() {
	    // arrange
		User user = mock(User.class);
		Schedule schedule = mock(Schedule.class);
		Seat seat = mock(Seat.class);

		Reservation reservation = Reservation.create(user, schedule, seat);
		reservation.reserve(LocalDateTime.now().plusMinutes(5), LocalDateTime.now());
		reservation.cancel();

		// act & assert
		assertThatThrownBy(reservation::cancel)
			.isInstanceOf(BusinessException.class)
			.hasMessageContaining("이미 취소된 좌석입니다.");
	}

	@Test
	@DisplayName("예약취소시 예약상태가 CANCEL이라면 예외가 발생한다.")
	void cancel_confirm_exception() {
		// arrange
		User user = mock(User.class);
		Schedule schedule = mock(Schedule.class);
		Seat seat = mock(Seat.class);

		Reservation reservation = Reservation.create(user, schedule, seat);
		reservation.reserve(LocalDateTime.now().plusMinutes(5), LocalDateTime.now());
		reservation.validateNotExpired(LocalDateTime.now());

		// act & assert
		assertThatThrownBy(reservation::cancel)
			.isInstanceOf(BusinessException.class)
			.hasMessageContaining("이미 확정된 좌석입니다.");
	}

	@Test
	@DisplayName("아직 오픈되지 않은 스케줄을 예약시 예외가 발생한다.")
	void schedule_opened_exception() {
	    // arrange
		User user = mock(User.class);
		Concert concert = mock(Concert.class);
		Schedule schedule = new Schedule(concert, LocalDate.now(), LocalDateTime.now().plusMinutes(5));
		Seat seat = mock(Seat.class);

		Reservation reservation = Reservation.create(user, schedule, seat);
	    // act & assert
	    assertThatThrownBy(() -> reservation.reserve(LocalDateTime.now().plusMinutes(5), LocalDateTime.now()))
			.isInstanceOf(BusinessException.class)
			.hasMessageContaining("아직 오픈되지 않은 스케줄입니다.");
	}
}
