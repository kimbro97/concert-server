package kr.hhplus.be.server.service.concert;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
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

	@Autowired
	private ConcertService concertService;

	@Autowired
	private SeatJpaRepository seatJpaRepository;

	@Autowired
	private UserJpaRepository userJpaRepository;

	@Autowired
	private ConcertJpaRepository concertJpaRepository;

	@Autowired
	private ScheduleJpaRepository scheduleJpaRepository;

	@Autowired
	private StringRedisTemplate stringRedisTemplate;

	@BeforeEach
	void setUp() {
		Set<String> keys = stringRedisTemplate.keys("concert:*:schedule:*:count");
		if (keys != null && !keys.isEmpty()) {
			stringRedisTemplate.delete(keys);
		}
		Set<String> sortedSet = stringRedisTemplate.keys("concert:ranking:*");
		if (sortedSet != null && !sortedSet.isEmpty()) {
			stringRedisTemplate.delete(sortedSet);
		}
	}

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

		Schedule schedule1 = new Schedule(concert1, LocalDate.of(2025, 4, 1), LocalDateTime.now());
		Schedule schedule2 = new Schedule(concert1, LocalDate.of(2025, 4, 2), LocalDateTime.now());
		Schedule schedule3 = new Schedule(concert1, LocalDate.of(2025, 4, 3), LocalDateTime.now());
		Schedule schedule4 = new Schedule(concert1, LocalDate.of(2025, 4, 4), LocalDateTime.now());
		Schedule schedule5 = new Schedule(concert2, LocalDate.of(2025, 4, 4), LocalDateTime.now());
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

		Schedule schedule = new Schedule(concert, LocalDate.of(2025, 4, 1), LocalDateTime.now());
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

	@Test
	@DisplayName("예약 좌석 수가 스케줄 좌석 수와 다르면 랭킹을 갱신하지 않는다.")
	void add_ranking_increase_success() {
	    // arrange
		Concert concert = new Concert("아이유 10주년 콘서트");
		concertJpaRepository.save(concert);

		Schedule schedule = new Schedule(concert, LocalDate.of(2025, 4, 1), LocalDateTime.now());
		scheduleJpaRepository.save(schedule);

		Seat seat1 = new Seat(schedule, "1", 1000L, false);
		Seat seat2 = new Seat(schedule, "2", 1000L, false);

		seatJpaRepository.saveAll(List.of(seat1, seat2));
		LocalDateTime today = LocalDateTime.now();

		ConcertCommand.AddRanking command = ConcertCommand.AddRanking
			.builder()
			.paymentId(1L)
			.concertId(concert.getId())
			.scheduleId(schedule.getId())
			.openedAt(today)
			.scheduleDate(LocalDate.now())
			.today(today)
			.build();
		// act
		concertService.addRanking(command);
	    // assert
		String count = stringRedisTemplate.opsForValue()
			.get("concert:" + concert.getId() + ":schedule:" + schedule.getId() + ":count");
		assertThat(count).isEqualTo("1");

		String ranking = stringRedisTemplate.opsForValue()
			.get("concert:ranking:" + today.format(DateTimeFormatter.BASIC_ISO_DATE));

		assertThat(ranking).isNull();
	}

	@Test
	@DisplayName("예약 좌석 수가 스케줄 좌석 수와 같으면 랭킹을 갱신한다.")
	void add_ranking_success() {
		// arrange
		Concert concert = new Concert("아이유 10주년 콘서트");
		concertJpaRepository.save(concert);

		Schedule schedule = new Schedule(concert, LocalDate.of(2025, 4, 1), LocalDateTime.now());
		scheduleJpaRepository.save(schedule);

		Seat seat1 = new Seat(schedule, "1", 1000L, false);

		seatJpaRepository.saveAll(List.of(seat1));
		LocalDateTime today = LocalDateTime.now();

		ConcertCommand.AddRanking command = ConcertCommand.AddRanking
			.builder()
			.paymentId(1L)
			.concertId(concert.getId())
			.scheduleId(schedule.getId())
			.openedAt(today)
			.scheduleDate(LocalDate.now())
			.today(today)
			.build();
		// act
		concertService.addRanking(command);
		// assert
		String count = stringRedisTemplate.opsForValue()
			.get("concert:" + concert.getId() + ":schedule:" + schedule.getId() + ":count");
		assertThat(count).isEqualTo("1");

		Set<String> range = stringRedisTemplate.opsForZSet()
			.range("concert:ranking:" + today.format(DateTimeFormatter.BASIC_ISO_DATE), 0, 15);

		assertThat(range).hasSize(1);
	}

}
