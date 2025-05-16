package kr.hhplus.be.server.service.reservation;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import kr.hhplus.be.server.domain.concert.Concert;
import kr.hhplus.be.server.domain.concert.Schedule;
import kr.hhplus.be.server.domain.concert.Seat;
import kr.hhplus.be.server.domain.reservation.Reservation;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.infras.concert.ConcertJpaRepository;
import kr.hhplus.be.server.infras.concert.ScheduleJpaRepository;
import kr.hhplus.be.server.infras.concert.SeatJpaRepository;
import kr.hhplus.be.server.infras.reservation.ReservationJpaRepository;
import kr.hhplus.be.server.infras.user.UserJpaRepository;

@SpringBootTest
public class ReservationServiceConcurrentTests {

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
	@DisplayName("동시에 좌석 예약을 시도하면 하나만 성공해야한다.")
	void concurrent_reservation() throws InterruptedException {
		// arrange
		User user = new User("kimbro", "1234");
		userJpaRepository.save(user);

		Concert concert = new Concert("아이유 10주년 콘서트");
		concertJpaRepository.save(concert);

		Schedule schedule = new Schedule(concert, LocalDate.now(), LocalDateTime.now());
		scheduleJpaRepository.save(schedule);

		Seat seat = new Seat(schedule, "A1", 1000L, true);
		seatJpaRepository.save(seat);

		ReservationCommand command = new ReservationCommand(user.getId(), schedule.getId(), seat.getId());

		int threadCount = 10;
		ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
		CountDownLatch latch = new CountDownLatch(threadCount);

		AtomicInteger successCount = new AtomicInteger(0);
		AtomicInteger failCount = new AtomicInteger(0);

		for (int i = 0; i < threadCount; i++) {
			executorService.submit(() -> {
				try {
					reservationService.reserve(command);
					successCount.incrementAndGet();
				} catch (Exception e) {
					failCount.incrementAndGet();
				} finally {
					latch.countDown();
				}
			});
		}

		latch.await();

		// assert
		List<Reservation> reservations = reservationJpaRepository.findAll();
		assertThat(reservations).hasSize(1);
		assertThat(successCount.get()).isEqualTo(1);
		assertThat(failCount.get()).isEqualTo(9);
	}
}
