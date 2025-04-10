package kr.hhplus.be.server.service.reservation;

import static kr.hhplus.be.server.support.exception.BusinessError.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import kr.hhplus.be.server.domain.concert.Concert;
import kr.hhplus.be.server.domain.concert.ConcertRepository;
import kr.hhplus.be.server.domain.concert.Schedule;
import kr.hhplus.be.server.domain.concert.Seat;
import kr.hhplus.be.server.domain.reservation.Reservation;
import kr.hhplus.be.server.domain.reservation.ReservationRepository;
import kr.hhplus.be.server.domain.reservation.ReservationStatus;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import kr.hhplus.be.server.support.exception.BusinessException;

@ExtendWith(MockitoExtension.class)
class ReservationServiceUnitTest {

	@Mock UserRepository userRepository;
	@Mock ConcertRepository concertRepository;
	@Mock ReservationRepository reservationRepository;

	@InjectMocks ReservationService reservationService;

	@Test
	@DisplayName("정상적인 예약 요청 시 예약 정보가 반환된다")
	void reserve_성공() {
		// arrange
		Long userId = 1L;
		Long scheduleId = 2L;
		Long seatId = 3L;
		ReservationCommand command = new ReservationCommand(userId, scheduleId, seatId);

		User user = new User("testUser", "1234");
		Schedule schedule = new Schedule(new Concert("방탄콘서트"), LocalDate.now());
		Seat seat = new Seat(schedule, "A1", 10000L, true);

		when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		when(concertRepository.findScheduleById(scheduleId)).thenReturn(Optional.of(schedule));
		when(concertRepository.findSeatById(seatId)).thenReturn(Optional.of(seat));
		when(reservationRepository.save(any(Reservation.class)))
			.thenAnswer(invocation -> invocation.getArgument(0));

		// act
		ReservationInfo result = reservationService.reserve(command);

		// assert
		assertThat(result).isNotNull();
		assertThat(result.getTotalAmount()).isEqualTo(10000L);
		assertThat(result.getStatus()).isEqualTo(ReservationStatus.RESERVED.toString());
	}
	@Test
	@DisplayName("존재하지 않는 유저 ID로 예약을 요청하면 예외가 발생한다")
	void reserve_유저없음() {
		// arrange
		Long userId = 1L;
		ReservationCommand command = new ReservationCommand(userId, 2L, 3L);

		when(userRepository.findById(userId)).thenReturn(Optional.empty());

		// act & assert
		assertThatThrownBy(() -> reservationService.reserve(command))
			.isInstanceOf(BusinessException.class)
			.hasMessageContaining(NOT_FOUND_USER_ERROR.getMessage());

		verify(userRepository).findById(userId);
		verify(concertRepository, never()).findScheduleById(any());
		verify(concertRepository, never()).findSeatById(any());
		verify(reservationRepository, never()).save(any());
	}

	@Test
	@DisplayName("존재하지 않는 스케줄 ID로 예약을 요청하면 예외가 발생한다")
	void reserve_스케줄없음() {
		// arrange
		Long scheduleId = 2L;
		ReservationCommand command = new ReservationCommand(1L, scheduleId, 3L);

		when(userRepository.findById(1L)).thenReturn(Optional.of(mock(User.class)));
		when(concertRepository.findScheduleById(scheduleId)).thenReturn(Optional.empty());

		// act & assert
		assertThatThrownBy(() -> reservationService.reserve(command))
			.isInstanceOf(BusinessException.class)
			.hasMessageContaining(NOT_FOUND_SCHEDULE_ERROR.getMessage());

		verify(concertRepository, never()).findSeatById(any());
		verify(reservationRepository, never()).save(any());
	}

	@Test
	@DisplayName("존재하지 않는 좌석 ID로 예약을 요청하면 예외가 발생한다")
	void reserve_좌석없음() {
		// arrange
		Long seatId = 3L;
		ReservationCommand command = new ReservationCommand(1L, 2L, seatId);

		when(userRepository.findById(1L)).thenReturn(Optional.of(mock(User.class)));
		when(concertRepository.findScheduleById(2L)).thenReturn(Optional.of(mock(Schedule.class)));
		when(concertRepository.findSeatById(seatId)).thenReturn(Optional.empty());

		// act & assert
		assertThatThrownBy(() -> reservationService.reserve(command))
			.isInstanceOf(BusinessException.class)
			.hasMessageContaining(NOT_FOUND_SEAT_ERROR.getMessage());

		verify(reservationRepository, never()).save(any());
	}
}
