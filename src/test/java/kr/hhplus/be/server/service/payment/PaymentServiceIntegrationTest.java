package kr.hhplus.be.server.service.payment;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;

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
import kr.hhplus.be.server.infras.token.TokenRedisRepository;
import kr.hhplus.be.server.infras.user.UserJpaRepository;
import kr.hhplus.be.server.support.exception.BusinessException;

@Transactional
@SpringBootTest
class PaymentServiceIntegrationTest {

	@Autowired
	private PaymentService paymentService;

	@Autowired
	private UserJpaRepository userJpaRepository;

	@Autowired
	private SeatJpaRepository seatJpaRepository;

	@Autowired
	private TokenRedisRepository tokenRedisRepository;

	@Autowired
	private StringRedisTemplate stringRedisTemplate;

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
	@DisplayName("결제시 유저가 존재하지 않으면 예외가 발생한다.")
	void payment_user_exception() {
		// arrange
		Long userId = 1L;
		Long reservationId = 1L;
		String uuid = "uuid_1";
		PaymentCommand command = new PaymentCommand(userId, reservationId, uuid, LocalDateTime.now());

		// act & assert
		assertThatThrownBy(() -> paymentService.pay(command))
			.isInstanceOf(BusinessException.class)
			.hasMessageContaining("유저를 찾을 수 없습니다.");
	}

	@Test
	@DisplayName("결제시 유저가 존재하고 예약건이 존재하지 않으면 예외가 발생한다.")
	void payment_reservation_exception() {
		// arrange
		User user = new User("kimbro", "1234");
		userJpaRepository.save(user);
		Long reservationId = 1L;
		String uuid = "uuid_1";
		PaymentCommand command = new PaymentCommand(user.getId(), reservationId, uuid, LocalDateTime.now());

		// act & assert
		assertThatThrownBy(() -> paymentService.pay(command))
			.isInstanceOf(BusinessException.class)
			.hasMessageContaining("예약 항목을 찾을 수 없습니다.");
	}

	@Test
	@DisplayName("결제시 유저와 예약이 존재하고 잔액이 존재하지 않으면 예외가 발생한다.")
	void payment_balance_exception() {
		// arrange
		User user = new User("kimbro", "1234");
		userJpaRepository.save(user);

		Concert concert = new Concert("아이유 10주년 콘서트");
		concertJpaRepository.save(concert);

		Schedule schedule = new Schedule(concert, LocalDate.now(), LocalDateTime.now());
		scheduleJpaRepository.save(schedule);

		Seat seat = new Seat(schedule, "A1", 1000L, true);
		seatJpaRepository.save(seat);

		Reservation reservation = Reservation.create(user, schedule, seat);
		reservation.reserve(LocalDateTime.now().plusMinutes(5), LocalDateTime.now());
		reservationJpaRepository.save(reservation);

		String uuid = "uuid_1";
		PaymentCommand command = new PaymentCommand(user.getId(), reservation.getId(), uuid, LocalDateTime.now());

		// act & assert
		assertThatThrownBy(() -> paymentService.pay(command))
			.isInstanceOf(BusinessException.class)
			.hasMessageContaining("발란스를 찾을 수 없습니다.");
	}

	@Test
	@DisplayName("결제시 예약시간이 5분 경과했다면 예외가 발생한다.")
	void payment_reserve_exception() {
		// arrange
		User user = new User("kimbro", "1234");
		userJpaRepository.save(user);

		Concert concert = new Concert("아이유 10주년 콘서트");
		concertJpaRepository.save(concert);

		Schedule schedule = new Schedule(concert, LocalDate.now(), LocalDateTime.now());
		scheduleJpaRepository.save(schedule);

		Seat seat = new Seat(schedule, "A1", 1000L, true);
		seatJpaRepository.save(seat);

		Balance balance = new Balance(user, 1000L);
		balanceJpaRepository.save(balance);

		Reservation reservation = Reservation.create(user, schedule, seat);
		reservation.reserve(LocalDateTime.of(2025, 4, 17, 16, 35), LocalDateTime.now());
		reservationJpaRepository.save(reservation);

		String uuid = "uuid_1";
		PaymentCommand command = new PaymentCommand(user.getId(), reservation.getId(), uuid, LocalDateTime.of(2025, 4, 17, 16, 41));

		// act & assert
		assertThatThrownBy(() -> paymentService.pay(command))
			.isInstanceOf(BusinessException.class)
			.hasMessageContaining("예약시간이 만료되어 결제하실 수 없습니다.");
	}

	@Test
	@DisplayName("결제시 보유금액보다 결제금액이 크면 예외가 발생한다.")
	void payment_balance_amount_exception() {
		// arrange
		User user = new User("kimbro", "1234");
		userJpaRepository.save(user);

		Concert concert = new Concert("아이유 10주년 콘서트");
		concertJpaRepository.save(concert);

		Schedule schedule = new Schedule(concert, LocalDate.now(), LocalDateTime.now());
		scheduleJpaRepository.save(schedule);

		Seat seat = new Seat(schedule, "A1", 10000L, true);
		seatJpaRepository.save(seat);

		Balance balance = new Balance(user, 1000L);
		balanceJpaRepository.save(balance);

		Reservation reservation = Reservation.create(user, schedule, seat);
		reservation.reserve(LocalDateTime.of(2025, 4, 17, 16, 35), LocalDateTime.now());
		reservationJpaRepository.save(reservation);

		String uuid = "uuid_1";
		PaymentCommand command = new PaymentCommand(user.getId(), reservation.getId(), uuid, LocalDateTime.of(2025, 4, 17, 16, 30));

		// act & assert
		assertThatThrownBy(() -> paymentService.pay(command))
			.isInstanceOf(BusinessException.class)
			.hasMessageContaining("회원님의 잔액이 부족합니다.");
	}

	@Test
	@DisplayName("예외가 발생하지 않으면 대기열토큰 삭제, 잔액차감, 결제정보가 저장된다.")
	void payment_success() {
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
		PaymentInfo info = paymentService.pay(command);

		// assert
		Payment payment = paymentJpaRepository.findById(info.getPaymentId()).orElseThrow();
		Optional<Token> deleteToken = tokenRedisRepository.findByUuid(uuid);
		Balance findBalance = balanceJpaRepository.findByUserId(user.getId()).orElseThrow();
		Long size = stringRedisTemplate.opsForZSet()
			.size("concert:ranking:" + LocalDateTime.now().format(DateTimeFormatter.BASIC_ISO_DATE));

		assertThat(payment.getId()).isEqualTo(info.getPaymentId());
		assertThat(deleteToken).isEmpty();
		assertThat(findBalance.getAmount()).isEqualTo(100L);
		assertThat(size).isEqualTo(1L);
	}
}
