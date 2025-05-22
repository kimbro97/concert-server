package kr.hhplus.be.server.infras.payment;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;
import org.springframework.transaction.annotation.Transactional;

import kr.hhplus.be.server.domain.balance.Balance;
import kr.hhplus.be.server.domain.concert.Concert;
import kr.hhplus.be.server.domain.concert.Schedule;
import kr.hhplus.be.server.domain.concert.Seat;
import kr.hhplus.be.server.domain.payment.PaymentCompletedEvent;
import kr.hhplus.be.server.domain.reservation.Reservation;
import kr.hhplus.be.server.domain.token.Token;
import kr.hhplus.be.server.domain.token.TokenStatus;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.infras.balance.BalanceJpaRepository;
import kr.hhplus.be.server.infras.concert.ConcertJpaRepository;
import kr.hhplus.be.server.infras.concert.ScheduleJpaRepository;
import kr.hhplus.be.server.infras.concert.SeatJpaRepository;
import kr.hhplus.be.server.infras.reservation.ReservationJpaRepository;
import kr.hhplus.be.server.infras.token.TokenRedisRepository;
import kr.hhplus.be.server.infras.user.UserJpaRepository;
import kr.hhplus.be.server.service.payment.PaymentCommand;
import kr.hhplus.be.server.service.payment.PaymentService;

@Transactional
@SpringBootTest
@RecordApplicationEvents
class SpringPaymentPublisherTest {

	@Autowired
	private PaymentService paymentService;

	@Autowired
	private ApplicationEvents applicationEvents;

	@Autowired
	private UserJpaRepository userJpaRepository;

	@Autowired
	private SeatJpaRepository seatJpaRepository;

	@Autowired
	private TokenRedisRepository tokenRedisRepository;

	@Autowired
	private ConcertJpaRepository concertJpaRepository;

	@Autowired
	private BalanceJpaRepository balanceJpaRepository;

	@Autowired
	private ScheduleJpaRepository scheduleJpaRepository;

	@Autowired
	private ReservationJpaRepository reservationJpaRepository;

	@Test
	@DisplayName("결제 성공 시 PaymentCompletedEvent 이벤트가 한 번 발행된다")
	void publish_payment_completed_event() {

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
		tokenRedisRepository.save(token);

		Reservation reservation = Reservation.create(user, schedule, seat);
		reservation.reserve(LocalDateTime.of(2025, 4, 17, 16, 35), LocalDateTime.now());
		reservationJpaRepository.save(reservation);

		PaymentCommand command = new PaymentCommand(user.getId(), reservation.getId(), uuid, LocalDateTime.of(2025, 4, 17, 16, 30));

		// act
		paymentService.pay(command);

		// assert
		long count = applicationEvents.stream(PaymentCompletedEvent.class).count();
		assertThat(count).isEqualTo(1);
	}
}
