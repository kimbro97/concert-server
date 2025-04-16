package kr.hhplus.be.server.service.concert;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import kr.hhplus.be.server.domain.concert.Concert;
import kr.hhplus.be.server.domain.concert.Schedule;
import kr.hhplus.be.server.domain.concert.Seat;
import kr.hhplus.be.server.infras.concert.ConcertJpaRepository;
import kr.hhplus.be.server.infras.concert.ScheduleJpaRepository;
import kr.hhplus.be.server.infras.concert.SeatJpaRepository;
import kr.hhplus.be.server.infras.user.UserJpaRepository;
import kr.hhplus.be.server.support.exception.BusinessException;

@Transactional
@SpringBootTest
class ConcertServiceIntegrationTest {

	@Autowired ConcertService concertService;
	@Autowired SeatJpaRepository seatJpaRepository;
	@Autowired UserJpaRepository userJpaRepository;
	@Autowired ConcertJpaRepository concertJpaRepository;
	@Autowired ScheduleJpaRepository scheduleJpaRepository;

	@Test
	@DisplayName("스케줄 조회시 존재하지 않는 콘서트면 예외가 발생한다")
	void get_schedule_user_exception() {
	    // arrange
		Long concertId = 1L;
		LocalDate startDate = LocalDate.of(2020, 1, 1);
		LocalDate endDate = LocalDate.of(2020, 1, 1);
		ConcertCommand.Schedule command = new ConcertCommand.Schedule(concertId, startDate, endDate);
		// act & assert
		assertThatThrownBy(() -> concertService.getSchedule(command))
			.isInstanceOf(BusinessException.class)
			.hasMessageContaining("콘서트 항목을 찾을 수 없습니다.");
	}

	@Test
	@DisplayName("예외가 발생하지 않으면 startDate와 endDate 사이의 스케줄이 조회된다")
	void get_schedule_success() {
		// arrange
		Concert concert1 = new Concert("아이유 10주년 콘서트");
		concertJpaRepository.save(concert1);
		Concert concert2 = new Concert("DG 10주년 콘서트");
		concertJpaRepository.save(concert2);

		Schedule schedule1 = new Schedule(concert1, LocalDate.of(2025, 4, 1));
		Schedule schedule2 = new Schedule(concert1, LocalDate.of(2025, 4, 2));
		Schedule schedule3 = new Schedule(concert1, LocalDate.of(2025, 4, 3));
		Schedule schedule4 = new Schedule(concert1, LocalDate.of(2025, 4, 4));
		Schedule schedule5 = new Schedule(concert2, LocalDate.of(2025, 4, 4));
		scheduleJpaRepository.saveAll(List.of(schedule1, schedule2, schedule3, schedule4,schedule5));

		LocalDate startDate = LocalDate.of(2025, 4, 2);
		LocalDate endDate = LocalDate.of(2025, 4, 30);
		ConcertCommand.Schedule command = new ConcertCommand.Schedule(concert1.getId(), startDate, endDate);

		// act
		List<ConcertInfo.ScheduleInfo> info = concertService.getSchedule(command);

		//assert
		assertThat(info.size()).isEqualTo(3);
	}

	@Test
	@DisplayName("좌석 조회시 존재하지 않는 스케줄이면 예외가 발생한다.")
	void get_seat_schedule_exception() {
	    // arrange
		Long scheduleId = 1L;
		ConcertCommand.Seat command = new ConcertCommand.Seat(scheduleId);

		// act & assert
		assertThatThrownBy(() -> concertService.getSeat(command))
			.isInstanceOf(BusinessException.class)
			.hasMessageContaining("스케줄 항목을 찾을 수 없습니다.");
	}

	@Test
	@DisplayName("예외가 발생하지않으면 정상적으로 좌석이 조회된다.")
	void get_seat_success() {
	    // arrange
		Concert concert = new Concert("아이유 10주년 콘서트");
		concertJpaRepository.save(concert);

		Schedule schedule = new Schedule(concert, LocalDate.of(2025, 4, 1));
		scheduleJpaRepository.save(schedule);

		Seat seat1 = new Seat(schedule, "1", 1000L, false);
		Seat seat2 = new Seat(schedule, "1", 1000L, false);
		Seat seat3 = new Seat(schedule, "1", 1000L, false);
		Seat seat4 = new Seat(schedule, "1", 1000L, false);

		seatJpaRepository.saveAll(List.of(seat1, seat2, seat3, seat4));

		ConcertCommand.Seat command = new ConcertCommand.Seat(schedule.getId());

		// act
		List<ConcertInfo.SeatInfo> info = concertService.getSeat(command);

		// assert
		assertThat(info.size()).isEqualTo(4);
	}

}
