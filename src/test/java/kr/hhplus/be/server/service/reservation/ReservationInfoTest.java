package kr.hhplus.be.server.service.reservation;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import kr.hhplus.be.server.domain.concert.Concert;
import kr.hhplus.be.server.domain.concert.Schedule;
import kr.hhplus.be.server.domain.concert.Seat;
import kr.hhplus.be.server.domain.reservation.Reservation;
import kr.hhplus.be.server.domain.user.User;

class ReservationInfoTest {
	@Test
	@DisplayName("Reservation 객체로부터 ReservationInfo를 생성할 수 있다")
	void from_정상_매핑() {
		// arrange
		User user = new User("testUser", "1234");
		Schedule schedule = new Schedule(mock(Concert.class), LocalDate.now());
		Seat seat = new Seat(schedule, "A1", 12000L, true);
		Reservation reservation = Reservation.create(user, schedule, seat);

		reservation.reserve();

		// act
		ReservationInfo info = ReservationInfo.from(reservation);

		// assert
		assertThat(info).isNotNull();
		assertThat(info.getReservationId()).isEqualTo(reservation.getId());
		assertThat(info.getUserId()).isEqualTo(user.getId());
		assertThat(info.getScheduleId()).isEqualTo(schedule.getId());
		assertThat(info.getSeatId()).isEqualTo(seat.getId());
		assertThat(info.getTotalAmount()).isEqualTo(reservation.getTotalAmount());
		assertThat(info.getStatus()).isEqualTo(reservation.getStatus().toString());
		assertThat(info.getExpiredAt()).isEqualTo(reservation.getExpiredAt());
	}
}
