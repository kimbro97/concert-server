package kr.hhplus.be.server.service.payment;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import kr.hhplus.be.server.domain.balance.Balance;
import kr.hhplus.be.server.domain.concert.Concert;
import kr.hhplus.be.server.domain.concert.Schedule;
import kr.hhplus.be.server.domain.concert.Seat;
import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.reservation.Reservation;
import kr.hhplus.be.server.domain.token.Token;
import kr.hhplus.be.server.domain.token.TokenStatus;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.infras.balance.BalanceJpaRepository;
import kr.hhplus.be.server.infras.concert.ConcertJpaRepository;
import kr.hhplus.be.server.infras.concert.ScheduleJpaRepository;
import kr.hhplus.be.server.infras.concert.SeatJpaRepository;
import kr.hhplus.be.server.infras.payment.PaymentJpaRepository;
import kr.hhplus.be.server.infras.reservation.ReservationJpaRepository;
import kr.hhplus.be.server.infras.token.TokenJpaRepository;
import kr.hhplus.be.server.infras.user.UserJpaRepository;

@SpringBootTest
class PaymentServiceConcurrentTests {

	@Autowired
	private PaymentService paymentService;

	@Autowired
	private UserJpaRepository userJpaRepository;

	@Autowired
	private SeatJpaRepository seatJpaRepository;

	@Autowired
	private TokenJpaRepository tokenJpaRepository;

	@Autowired
	private ConcertJpaRepository concertJpaRepository;

	@Autowired
	private BalanceJpaRepository balanceJpaRepository;

	@Autowired
	private PaymentJpaRepository paymentJpaRepository;

	@Autowired
	private ScheduleJpaRepository scheduleJpaRepository;

	@Autowired
	private ReservationJpaRepository reservationJpaRepository;

	@Test
	@DisplayName("한명의 유저가 동시에 2번 결제시 하나의 결제만 성공한다.")
	void payment_concurrent_success() throws InterruptedException {
		// arrange
		String uuid = "uuid_1";

		User user = new User("kimbro", "1234");
		userJpaRepository.save(user);

		Concert concert = new Concert("아이유 10주년 콘서트");
		concertJpaRepository.save(concert);

		Schedule schedule = new Schedule(concert, LocalDate.now(), LocalDateTime.now());
		scheduleJpaRepository.save(schedule);

		Seat seat = new Seat(schedule, "A1", 900L, true);
		seatJpaRepository.save(seat);

		Balance balance = new Balance(user, 1000L);
		balanceJpaRepository.save(balance);

		Token token = Token.create(user, schedule, uuid, TokenStatus.ACTIVE);
		tokenJpaRepository.save(token);

		Reservation reservation = Reservation.create(user, schedule, seat);
		reservation.reserve(LocalDateTime.of(2025, 4, 17, 16, 35), LocalDateTime.now());
		reservationJpaRepository.save(reservation);

		PaymentCommand command = new PaymentCommand(user.getId(), reservation.getId(), uuid, LocalDateTime.of(2025, 4, 17, 16, 30));

		int threadCount = 2;
		ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
		CountDownLatch latch = new CountDownLatch(threadCount);

		AtomicInteger successCount = new AtomicInteger(0);
		AtomicInteger failCount = new AtomicInteger(0);

		// act
		for (int i = 0; i < threadCount; i++) {
			executorService.submit(() -> {
				try {
					paymentService.pay(command);
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
		List<Payment> payments = paymentJpaRepository.findAll();
		Balance balance1 = balanceJpaRepository.findByUserId(user.getId()).orElseThrow();
		assertThat(payments).hasSize(1);
		assertThat(successCount.get()).isEqualTo(1);
		assertThat(failCount.get()).isEqualTo(1);
		assertThat(balance1.getAmount()).isEqualTo(100L);
	}
}
