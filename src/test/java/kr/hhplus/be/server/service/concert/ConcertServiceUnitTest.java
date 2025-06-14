package kr.hhplus.be.server.service.concert;

import static kr.hhplus.be.server.support.exception.BusinessError.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
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
import kr.hhplus.be.server.support.exception.BusinessException;

@ExtendWith(MockitoExtension.class)
class ConcertServiceUnitTest {

	@Mock
	ConcertRepository concertRepository;

	@InjectMocks
	ConcertService concertService;


	@Test
	@DisplayName("콘서트 ID와 날짜 범위로 스케줄 목록을 조회한다")
	void getSchedule_성공() {

		Long concertId = 1L;
		LocalDate startDate = LocalDate.of(2025, 4, 10);
		LocalDate endDate = LocalDate.of(2025, 4, 30);
		Concert concert = new Concert("로미오");
		ConcertCommand.Schedule command = new ConcertCommand.Schedule(concertId, startDate, endDate);
		List<Schedule> schedules = List.of(new Schedule(concert, LocalDate.of(2025, 4, 15), LocalDateTime.now()));

		when(concertRepository.findConcertById(concertId)).thenReturn(Optional.of(concert));

		when(concertRepository.findAllByConcertIdAndDateBetween(concertId, startDate, endDate))
			.thenReturn(schedules);

		List<ConcertInfo.ScheduleInfo> scheduleInfos = concertService.getSchedule(command);

		assertThat(scheduleInfos.size()).isEqualTo(1);
		assertThat(scheduleInfos.get(0).getDate()).isEqualTo(LocalDate.of(2025, 4, 15));
	}

	@Test
	@DisplayName("존재하지 않는 콘서트 ID로 조회 시 예외가 발생한다")
	void getSchedule_콘서트없음() {
		Long concertId = 99L;
		LocalDate startDate = LocalDate.of(2025, 4, 10);
		LocalDate endDate = LocalDate.of(2025, 4, 30);
		ConcertCommand.Schedule command = new ConcertCommand.Schedule(concertId, startDate, endDate);

		when(concertRepository.findConcertById(concertId)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> concertService.getSchedule(command))
			.isInstanceOf(BusinessException.class)
			.hasMessageContaining(NOT_FOUND_CONCERT_ERROR.getMessage());

		verify(concertRepository, times(0)).findAllByConcertIdAndDateBetween(any(), any(), any());
	}

	@Test
	@DisplayName("스케줄 ID로 좌석 목록을 조회한다")
	void getSeat_성공() {
		Long scheduleId = 1L;
		Schedule schedule = mock(Schedule.class);
		ConcertCommand.Seat command = new ConcertCommand.Seat(scheduleId);

		when(concertRepository.findScheduleById(scheduleId)).thenReturn(Optional.of(schedule));

		List<Seat> seats = List.of(new Seat(schedule, "1", 1000L, true), new Seat(schedule, "2", 1000L, false));
		when(concertRepository.findAllSeatByScheduleId(scheduleId)).thenReturn(seats);

		List<ConcertInfo.SeatInfo> result = concertService.getSeat(command);

		assertThat(result).hasSize(2);
		assertThat(result.get(0).getNumber()).isEqualTo("1");
		assertThat(result.get(0).getPrice()).isEqualTo(1000L);
		assertThat(result.get(0).getIsSelectable()).isTrue();
	}

	@Test
	@DisplayName("존재하지 않는 스케줄 ID로 조회 시 예외가 발생한다")
	void getSeat_스케줄없음() {
		Long scheduleId = 999L;
		ConcertCommand.Seat command = new ConcertCommand.Seat(scheduleId);

		when(concertRepository.findScheduleById(scheduleId)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> concertService.getSeat(command))
			.isInstanceOf(BusinessException.class)
			.hasMessageContaining(NOT_FOUND_SCHEDULE_ERROR.getMessage());

		verify(concertRepository, times(0)).findAllSeatByScheduleId(any());
	}

	@Test
	@DisplayName("예약 좌석 수가 스케줄 좌석 수와 다르면 랭킹 갱신을 실행하지 않는다.")
	void add_rankin_increase_success() {
	    // arrange
		Long concertId = 1L;
		Long scheduleId = 2L;
		Long paymentId = 3L;
		LocalDate scheduleDate = LocalDate.now();
		LocalDateTime openedAt = LocalDateTime.now();
		LocalDateTime today = LocalDateTime.now();

		ConcertCommand.AddRanking command = ConcertCommand.AddRanking
			.builder()
			.today(today)
			.concertId(concertId)
			.scheduleDate(scheduleDate)
			.scheduleId(scheduleId)
			.paymentId(paymentId)
			.openedAt(openedAt)
			.build();

		when(concertRepository.incrementScheduleCount(concertId, scheduleId, today, scheduleDate)).thenReturn(39L);
		when(concertRepository.countByScheduleId(scheduleId)).thenReturn(50L);
	    // act
		concertService.addRanking(command);
	    // assert
		verify(concertRepository, times(1)).incrementScheduleCount(concertId, scheduleId, today, scheduleDate);
		verify(concertRepository, times(1)).countByScheduleId(scheduleId);
		verify(concertRepository, never()).addRanking(today, concertId, Duration.between(openedAt, today).toMillis());
	}

	@Test
	@DisplayName("예약 좌석 수가 스케줄 좌석 수와 일치하면 갱신을 실행한다.")
	void add_rankin_increase_and_ranking_success() {
		// arrange
		Long concertId = 1L;
		Long scheduleId = 2L;
		Long paymentId = 3L;
		LocalDate scheduleDate = LocalDate.now();
		LocalDateTime openedAt = LocalDateTime.now();
		LocalDateTime today = LocalDateTime.now();

		ConcertCommand.AddRanking command = ConcertCommand.AddRanking
			.builder()
			.today(today)
			.concertId(concertId)
			.scheduleDate(scheduleDate)
			.scheduleId(scheduleId)
			.paymentId(paymentId)
			.openedAt(openedAt)
			.build();

		when(concertRepository.incrementScheduleCount(concertId, scheduleId, today, scheduleDate)).thenReturn(50L);
		when(concertRepository.countByScheduleId(scheduleId)).thenReturn(50L);
		// act
		concertService.addRanking(command);
		// assert
		verify(concertRepository, times(1)).incrementScheduleCount(concertId, scheduleId, today, scheduleDate);
		verify(concertRepository, times(1)).countByScheduleId(scheduleId);
		verify(concertRepository, times(1)).addRanking(today, concertId, Duration.between(openedAt, today).toMillis());
	}

	@Test
	@DisplayName("addRanking 수행 중 예외 발생 시 이후 로직이 실행되지 않아야 한다")
	void add_rankin_exception() {
		// arrange
		Long concertId = 1L;
		Long scheduleId = 2L;
		Long paymentId = 3L;
		LocalDate scheduleDate = LocalDate.now();
		LocalDateTime openedAt = LocalDateTime.now();
		LocalDateTime today = LocalDateTime.now();

		ConcertCommand.AddRanking command = ConcertCommand.AddRanking
			.builder()
			.today(today)
			.concertId(concertId)
			.scheduleDate(scheduleDate)
			.scheduleId(scheduleId)
			.paymentId(paymentId)
			.openedAt(openedAt)
			.build();

		when(concertRepository.incrementScheduleCount(concertId, scheduleId, today, scheduleDate)).thenThrow(RuntimeException.class);
		// act
		concertService.addRanking(command);
		// assert
		verify(concertRepository, times(1)).incrementScheduleCount(concertId, scheduleId, today, scheduleDate);
		verify(concertRepository, never()).countByScheduleId(scheduleId);
		verify(concertRepository, never()).addRanking(today, concertId, Duration.between(openedAt, today).toMillis());
	}
}
