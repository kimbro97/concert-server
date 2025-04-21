package kr.hhplus.be.server.service.token;

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
import kr.hhplus.be.server.domain.token.Token;
import kr.hhplus.be.server.domain.token.TokenStatus;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.infras.concert.ConcertJpaRepository;
import kr.hhplus.be.server.infras.concert.ScheduleJpaRepository;
import kr.hhplus.be.server.infras.token.TokenJpaRepository;
import kr.hhplus.be.server.infras.user.UserJpaRepository;
import kr.hhplus.be.server.support.exception.BusinessException;

@Transactional
@SpringBootTest
class TokenServiceIntegrationTest {

	@Autowired
	private TokenService tokenService;

	@Autowired
	private UserJpaRepository userJpaRepository;

	@Autowired
	private ConcertJpaRepository concertJpaRepository;

	@Autowired
	private ScheduleJpaRepository scheduleJpaRepository;

	@Autowired
	private TokenJpaRepository tokenJpaRepository;

	@Test
	@DisplayName("토큰 생성시 유저가 존재하지 않으면 예외가 발생한다.")
	void token_create_user_exception() {
	    // arrange
		Long userId = 1L;
		Long scheduleId = 1L;
		TokenCommand command = new TokenCommand(userId, scheduleId);

		// act & assert
		assertThatThrownBy(() -> tokenService.createToken(command))
			.isInstanceOf(BusinessException.class)
			.hasMessageContaining("유저를 찾을 수 없습니다.");
	}

	@Test
	@DisplayName("토큰 생성시 유저가 존재하고 스케줄이 존재하지 않으면 예외가 발생한다.")
	void token_create_schedule_exception() {
		// arrange
		User user = new User("kimbro", "1234");
		userJpaRepository.save(user);
		Long scheduleId = 1L;
		TokenCommand command = new TokenCommand(user.getId(), scheduleId);

		// act & assert
		assertThatThrownBy(() -> tokenService.createToken(command))
			.isInstanceOf(BusinessException.class)
			.hasMessageContaining("스케줄 항목을 찾을 수 없습니다.");
	}

	@Test
	@DisplayName("토큰 생성시 예외가 발생하지않으면 정상적으로 저장된다.")
	void token_create_success() {
	    // arrange
		User user = new User("kimbro", "1234");
		userJpaRepository.save(user);

		Concert concert = new Concert("아이유 10주년 콘서트");
		concertJpaRepository.save(concert);

		Schedule schedule = new Schedule(concert, LocalDate.now());
		scheduleJpaRepository.save(schedule);

		TokenCommand command = new TokenCommand(user.getId(), schedule.getId());

		// act
		TokenInfo info = tokenService.createToken(command);
		// assert
		Token token = tokenJpaRepository.findById(info.getTokenId()).orElseThrow();
		assertThat(info.getTokenId()).isEqualTo(token.getId());
		assertThat(info.getTokenValue()).isEqualTo(token.getUuid());
	}

	@Test
	@DisplayName("토큰 위치 조회시 조회된 토큰이 ACTIVE 토큰이면 location = 1, status = ACTIVE를 반환한다.")
	void get_token_active_success() {
	    // arrange
		User user = new User("kimbro", "1234");
		userJpaRepository.save(user);

		Concert concert = new Concert("아이유 10주년 콘서트");
		concertJpaRepository.save(concert);

		Schedule schedule = new Schedule(concert, LocalDate.now());
		scheduleJpaRepository.save(schedule);

		Token token = Token.create(user, schedule, "uuid_1", TokenStatus.ACTIVE);
		tokenJpaRepository.save(token);

		TokenLocationCommand command = new TokenLocationCommand("uuid_1");
		// act
		TokenLocationInfo info = tokenService.getTokenLocation(command);
		// assert
		assertThat(info.getScheduleId()).isEqualTo(schedule.getId());
		assertThat(info.getLocation()).isEqualTo(1L);
		assertThat(info.getStatus()).isEqualTo(TokenStatus.ACTIVE.toString());
	}

	@Test
	@DisplayName("토큰 위치 조회시 조회된 토큰이 PENDING 토큰이면 해당 위치와 status = PENDING를 반환한다.")
	void get_token_pending_success() {
		// arrange
		User user = new User("kimbro", "1234");
		userJpaRepository.save(user);

		Concert concert = new Concert("아이유 10주년 콘서트");
		concertJpaRepository.save(concert);

		Schedule schedule = new Schedule(concert, LocalDate.now());
		scheduleJpaRepository.save(schedule);

		Token token1 = Token.create(user, schedule, "uuid_1", TokenStatus.PENDING);
		Token token2 = Token.create(user, schedule, "uuid_2", TokenStatus.PENDING);
		Token token3 = Token.create(user, schedule, "uuid_3", TokenStatus.PENDING);
		Token token4 = Token.create(user, schedule, "uuid_4", TokenStatus.PENDING);
		tokenJpaRepository.save(token1);
		tokenJpaRepository.save(token2);
		tokenJpaRepository.save(token3);
		tokenJpaRepository.save(token4);

		TokenLocationCommand command = new TokenLocationCommand("uuid_2");
		// act
		TokenLocationInfo info = tokenService.getTokenLocation(command);
		// assert
		assertThat(info.getScheduleId()).isEqualTo(schedule.getId());
		assertThat(info.getLocation()).isEqualTo(2L);
		assertThat(info.getStatus()).isEqualTo(TokenStatus.PENDING.toString());
	}

	@Test
	@DisplayName("해당 스케줄 대기열에 PENDING 토큰중 위치가 1번이고 ACTIVE 토큰이 1000개 이하라면 PENDING -> ACTIVE로 상태가 변경된다")
	void update_token_active_success_() {
		// arrange
		User user1 = new User("kimbro1", "1234");
		User user2 = new User("kimbro2", "1234");
		User user3 = new User("kimbro2", "1234");
		User user4 = new User("kimbro2", "1234");
		User user5 = new User("kimbro2", "1234");
		User user6 = new User("kimbro2", "1234");
		userJpaRepository.saveAll(List.of(user1, user2, user3, user4, user5, user6));

		Concert concert = new Concert("아이유 10주년 콘서트");
		concertJpaRepository.save(concert);

		Schedule schedule = new Schedule(concert, LocalDate.now());
		scheduleJpaRepository.save(schedule);

		Token token1 = Token.create(user1, schedule, "uuid_1", TokenStatus.PENDING);
		Token token2 = Token.create(user2, schedule, "uuid_2", TokenStatus.PENDING);
		Token token3 = Token.create(user3, schedule, "uuid_3", TokenStatus.PENDING);
		Token token4 = Token.create(user4, schedule, "uuid_4", TokenStatus.ACTIVE);
		Token token5 = Token.create(user5, schedule, "uuid_5", TokenStatus.ACTIVE);
		Token token6 = Token.create(user6, schedule, "uuid_6", TokenStatus.ACTIVE);
		tokenJpaRepository.saveAll(List.of(token1, token2, token3, token4, token5, token6));

		// act
		tokenService.activateToken();
		// assert
		Token token = tokenJpaRepository.findByUuid("uuid_1").orElseThrow();
		assertThat(token.getStatus()).isEqualTo(TokenStatus.ACTIVE);
	}

	@Test
	@DisplayName("유효시간이 지난 ACTIVE토큰을 삭제할 수 있다.")
	void expire_active_token_delete() {
		// arrange
		User user1 = new User("kimbro1", "1234");
		User user2 = new User("kimbro2", "1234");
		User user3 = new User("kimbro2", "1234");
		User user4 = new User("kimbro2", "1234");
		User user5 = new User("kimbro2", "1234");
		User user6 = new User("kimbro2", "1234");
		userJpaRepository.saveAll(List.of(user1, user2, user3, user4, user5, user6));

		Concert concert = new Concert("아이유 10주년 콘서트");
		concertJpaRepository.save(concert);

		Schedule schedule = new Schedule(concert, LocalDate.now());
		scheduleJpaRepository.save(schedule);

		Token token1 = Token.create(user1, schedule, "uuid_1", TokenStatus.PENDING);
		Token token2 = Token.create(user2, schedule, "uuid_2", TokenStatus.PENDING);
		Token token3 = Token.create(user3, schedule, "uuid_3", TokenStatus.PENDING);
		Token token4 = Token.create(user4, schedule, "uuid_4", TokenStatus.PENDING);
		Token token5 = Token.create(user5, schedule, "uuid_5", TokenStatus.PENDING);
		Token token6 = Token.create(user6, schedule, "uuid_6", TokenStatus.PENDING);
		tokenJpaRepository.saveAll(List.of(token1, token2, token3, token4, token5, token6));

		token1.activate(1L, 999L, LocalDateTime.of(2025, 4, 17, 13, 30));
		token2.activate(1L, 999L, LocalDateTime.of(2025, 4, 17, 13, 31));
		token3.activate(1L, 999L, LocalDateTime.of(2025, 4, 17, 13, 32));
		token4.activate(1L, 999L, LocalDateTime.of(2025, 4, 17, 13, 33));
		token5.activate(1L, 999L, LocalDateTime.of(2025, 4, 17, 13, 34));
		token6.activate(1L, 999L, LocalDateTime.of(2025, 4, 17, 13, 35));
		tokenJpaRepository.saveAll(List.of(token1, token2, token3, token4, token5, token6));
	    // act
		tokenService.expireToken(LocalDateTime.of(2025, 4, 17, 13, 33));
	    // assert
		List<Token> byStatus = tokenJpaRepository.findByStatus(TokenStatus.ACTIVE);
		assertThat(byStatus).hasSize(3);
	}


}
