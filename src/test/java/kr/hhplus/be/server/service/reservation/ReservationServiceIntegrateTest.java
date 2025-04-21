package kr.hhplus.be.server.service.reservation;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import kr.hhplus.be.server.domain.concert.Concert;
import kr.hhplus.be.server.domain.concert.Schedule;
import kr.hhplus.be.server.domain.concert.Seat;
import kr.hhplus.be.server.domain.reservation.Reservation;
import kr.hhplus.be.server.domain.reservation.ReservationStatus;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.infras.concert.ConcertJpaRepository;
import kr.hhplus.be.server.infras.concert.ScheduleJpaRepository;
import kr.hhplus.be.server.infras.concert.SeatJpaRepository;
import kr.hhplus.be.server.infras.reservation.ReservationJpaRepository;
import kr.hhplus.be.server.infras.user.UserJpaRepository;
import kr.hhplus.be.server.support.exception.BusinessException;

@Transactional
@SpringBootTest
class ReservationServiceIntegrateTest {

	@Autowired
	private UserJpaRepository userJpaRepository;

	@Autowired
	private SeatJpaRepository seatJpaRepository;

	@Autowired
	private ReservationService reservationService;

	@Autowired
	private ConcertJpaRepository concertJpaRepository;

	@Autowired
	private ScheduleJpaRepository scheduleJpaRepository;

	@Autowired
	private ReservationJpaRepository reservationJpaRepository;

	@Test
	@DisplayName("예약시 존재하지 않은 유저이면 예외가 발생한다.")
	void reserve_user_exception() {
	    // arrange
		Long userId = 1L;
		Long scheduleId = 1L;
		Long seatId = 1L;

		ReservationCommand command = new ReservationCommand(userId, scheduleId, seatId);

		// act & assert
		assertThatThrownBy(() -> reservationService.reserve(command))
			.isInstanceOf(BusinessException.class)
			.hasMessageContaining("유저를 찾을 수 없습니다.");
	}

	@Test
	@DisplayName("예약시 유저가 존재하고 스케줄이 존재하지 않으면 예외가 발생한다.")
	void reserve_schedule_exception() {
		// arrange
		User user = new User("kimbro", "1234");
		userJpaRepository.save(user);
		Long scheduleId = 1L;
		Long seatId = 1L;

		ReservationCommand command = new ReservationCommand(user.getId(), scheduleId, seatId);

		// act & assert
		assertThatThrownBy(() -> reservationService.reserve(command))
			.isInstanceOf(BusinessException.class)
			.hasMessageContaining("스케줄 항목을 찾을 수 없습니다.");
	}

	@Test
	@DisplayName("예약시 유저와 스케줄이 존재하고 좌석이 존재하지 않으면 예외가 발생한다.")
	void reserve_seat_exception() {
		// arrange
		User user = new User("kimbro", "1234");
		userJpaRepository.save(user);

		Concert concert = new Concert("아이유 10주년 콘서트");
		concertJpaRepository.save(concert);

		Schedule schedule = new Schedule(concert, LocalDate.now());
		scheduleJpaRepository.save(schedule);
		Long seatId = 1L;

		ReservationCommand command = new ReservationCommand(user.getId(), schedule.getId(), seatId);

		// act & assert
		assertThatThrownBy(() -> reservationService.reserve(command))
			.isInstanceOf(BusinessException.class)
			.hasMessageContaining("좌석을 찾을 수 없습니다.");
	}

	@Test
	@DisplayName("예약시 이미 예약된 좌석이면 예외가 발생한다.")
	void reserve_already_seat_exception() {
	    // arrange
		User user = new User("kimbro", "1234");
		userJpaRepository.save(user);

		Concert concert = new Concert("아이유 10주년 콘서트");
		concertJpaRepository.save(concert);

		Schedule schedule = new Schedule(concert, LocalDate.now());
		scheduleJpaRepository.save(schedule);

		Seat seat = new Seat(schedule, "A1", 1000L, false);
		seatJpaRepository.save(seat);

		ReservationCommand command = new ReservationCommand(user.getId(), schedule.getId(), seat.getId());

		// act & assert
		assertThatThrownBy(() -> reservationService.reserve(command))
			.isInstanceOf(BusinessException.class)
			.hasMessageContaining("이미 예약된 좌석입니다.");
	}

	@Test
	@DisplayName("예약시 예외가 발생하지 않으면 에약에 성공한다.")
	void reserve_success() {
		// arrange
		User user = new User("kimbro", "1234");
		userJpaRepository.save(user);

		Concert concert = new Concert("아이유 10주년 콘서트");
		concertJpaRepository.save(concert);

		Schedule schedule = new Schedule(concert, LocalDate.now());
		scheduleJpaRepository.save(schedule);

		Seat seat = new Seat(schedule, "A1", 1000L, true);
		seatJpaRepository.save(seat);

		ReservationCommand command = new ReservationCommand(user.getId(), schedule.getId(), seat.getId());

		// act
		ReservationInfo info = reservationService.reserve(command);

		// assert
		Reservation reservation = reservationJpaRepository.findById(info.getReservationId()).orElseThrow();
		assertThat(info.getReservationId()).isEqualTo(reservation.getId());
		assertThat(info.getSeatId()).isEqualTo(reservation.getSeat().getId());
		assertThat(info.getStatus()).isEqualTo(reservation.getStatus().toString());
		assertThat(info.getUserId()).isEqualTo(reservation.getUser().getId());
		assertThat(info.getScheduleId()).isEqualTo(reservation.getSchedule().getId());
		assertThat(info.getTotalAmount()).isEqualTo(reservation.getTotalAmount());
	}

	@Test
	@DisplayName("예약시간이 5분 경과하고 결제하지 않은 예약건은 취소된다.")
	void reserve_cancel() {
	    // arrange
		User user = new User("kimbro", "1234");
		userJpaRepository.save(user);

		Concert concert = new Concert("아이유 10주년 콘서트");
		concertJpaRepository.save(concert);

		Schedule schedule = new Schedule(concert, LocalDate.now());
		scheduleJpaRepository.save(schedule);

		Seat seat1 = new Seat(schedule, "A1", 1000L, true);
		Seat seat2 = new Seat(schedule, "A2", 1000L, true);
		Seat seat3 = new Seat(schedule, "A3", 1000L, true);
		Seat seat4 = new Seat(schedule, "A4", 1000L, true);
		Seat seat5 = new Seat(schedule, "A5", 1000L, true);
		seatJpaRepository.saveAll(List.of(seat1, seat2, seat3, seat4, seat5));

		Reservation reservation1 = Reservation.create(user, schedule, seat1);
		Reservation reservation2 = Reservation.create(user, schedule, seat2);
		Reservation reservation3 = Reservation.create(user, schedule, seat3);
		Reservation reservation4 = Reservation.create(user, schedule, seat4);
		Reservation reservation5 = Reservation.create(user, schedule, seat5);

		reservation1.reserve(LocalDateTime.of(2025, 4, 17, 3, 13));
		reservation1.validateNotExpired(LocalDateTime.of(2025, 4, 17, 3, 13));
		reservation2.reserve(LocalDateTime.of(2025, 4, 17, 3, 14));
		reservation3.reserve(LocalDateTime.of(2025, 4, 17, 3, 15));
		reservation4.reserve(LocalDateTime.of(2025, 4, 17, 3, 16));
		reservation5.reserve(LocalDateTime.of(2025, 4, 17, 3, 17));
		reservationJpaRepository.saveAll(List.of(reservation1, reservation2, reservation3, reservation4, reservation5));

		// act
		reservationService.cancel(LocalDateTime.of(2025, 4, 17, 3, 15));

	    // assert
		reservation1 = reservationJpaRepository.findById(reservation1.getId()).orElseThrow();
		reservation2 = reservationJpaRepository.findById(reservation2.getId()).orElseThrow();
		reservation3 = reservationJpaRepository.findById(reservation3.getId()).orElseThrow();
		reservation4 = reservationJpaRepository.findById(reservation4.getId()).orElseThrow();
		reservation5 = reservationJpaRepository.findById(reservation5.getId()).orElseThrow();

		assertThat(reservation1.getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
		assertThat(reservation2.getStatus()).isEqualTo(ReservationStatus.CANCEL);
		assertThat(reservation3.getStatus()).isEqualTo(ReservationStatus.RESERVED);
		assertThat(reservation4.getStatus()).isEqualTo(ReservationStatus.RESERVED);
		assertThat(reservation5.getStatus()).isEqualTo(ReservationStatus.RESERVED);

		assertThat(reservation1.getSeat().getIsSelectable()).isFalse();
		assertThat(reservation2.getSeat().getIsSelectable()).isTrue();
		assertThat(reservation3.getSeat().getIsSelectable()).isFalse();
		assertThat(reservation4.getSeat().getIsSelectable()).isFalse();
		assertThat(reservation5.getSeat().getIsSelectable()).isFalse();

	}
}
