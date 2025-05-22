package kr.hhplus.be.server.infras.token;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import kr.hhplus.be.server.domain.concert.Concert;
import kr.hhplus.be.server.domain.concert.Schedule;
import kr.hhplus.be.server.domain.token.Token;
import kr.hhplus.be.server.domain.token.TokenStatus;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.infras.concert.ConcertJpaRepository;
import kr.hhplus.be.server.infras.concert.ScheduleJpaRepository;
import kr.hhplus.be.server.infras.user.UserJpaRepository;

@SpringBootTest
class TokenRedisRepositoryTest {

	@Autowired
	private UserJpaRepository userJpaRepository;

	@Autowired
	private ConcertJpaRepository concertJpaRepository;

	@Autowired
	private ScheduleJpaRepository scheduleJpaRepository;

	@Autowired
	private StringRedisTemplate stringRedisTemplate;

	@Autowired
	private TokenRedisRepository tokenRedisRepository;

	@AfterEach
	void tearDown() {
		stringRedisTemplate.getConnectionFactory().getConnection().serverCommands().flushDb();
	}

	@Test
	@DisplayName("토큰을 받아 pending 상태의 토큰을 만들 수 있다.")
	void token_create () {
		// arrange
		User user = new User("kimbro", "asdf");
		Concert concert = new Concert("아이유 10주년 콘서트");
		Schedule schedule = new Schedule(concert, LocalDate.now(), LocalDateTime.now());
		String uuid = "uuid_1";

		userJpaRepository.save(user);
		concertJpaRepository.save(concert);
		scheduleJpaRepository.save(schedule);

		Token token = Token.create(user, schedule, uuid, TokenStatus.PENDING);
		// act
		tokenRedisRepository.save(token);
		// assert
		Set<String> range = stringRedisTemplate.opsForZSet().range("pending:schedule:" + schedule.getId(), 0, 1);
		assertThat(range).hasSize(1);
	}

	@Test
	@DisplayName("scheduleId와 uuid를 받아서 대기열 위치를 찾을 수 있다.")
	void find_location () {
		// arrange
		User user = new User("kimbro", "asdf");
		Concert concert = new Concert("아이유 10주년 콘서트");
		Schedule schedule = new Schedule(concert, LocalDate.now(), LocalDateTime.now());

		userJpaRepository.save(user);
		concertJpaRepository.save(concert);
		scheduleJpaRepository.save(schedule);

		Token token1 = Token.create(user, schedule, "uuid_1", TokenStatus.PENDING);
		Token token2 = Token.create(user, schedule, "uuid_2", TokenStatus.PENDING);
		Token token3 = Token.create(user, schedule, "uuid_3", TokenStatus.PENDING);

		tokenRedisRepository.save(token1);
		tokenRedisRepository.save(token2);
		tokenRedisRepository.save(token3);

		// act
		Long location = tokenRedisRepository.findTokenLocation(schedule.getId(), "uuid_2");
		// assert
		assertThat(location).isEqualTo(2);
	}

	@Test
	@DisplayName("uuid값을 받아서 token을 조회할 수 있다.")
	void find_by_uuid () {
		// arrange
		User user = new User("kimbro", "asdf");
		Concert concert = new Concert("아이유 10주년 콘서트");
		Schedule schedule = new Schedule(concert, LocalDate.now(), LocalDateTime.now());

		userJpaRepository.save(user);
		concertJpaRepository.save(concert);
		scheduleJpaRepository.save(schedule);

		Token token = Token.create(user, schedule, "uuid_1", TokenStatus.PENDING);
		tokenRedisRepository.save(token);
		// act
		Token findToken = tokenRedisRepository.findByUuid("uuid_1").orElseThrow();
		// assert
		assertThat(findToken).isNotNull();
		assertThat(findToken.getUuid()).isEqualTo("uuid_1");

	}

	@Test
	@DisplayName("uuid값을 받아서 token을 조회시 null이면 빈 Optional을 반환한다.")
	void find_by_uuid_null () {
		// arrange
		User user = new User("kimbro", "asdf");
		Concert concert = new Concert("아이유 10주년 콘서트");
		Schedule schedule = new Schedule(concert, LocalDate.now(), LocalDateTime.now());

		userJpaRepository.save(user);
		concertJpaRepository.save(concert);
		scheduleJpaRepository.save(schedule);

		// act
		Optional<Token> token = tokenRedisRepository.findByUuid("uuid_1");
		// assert
		assertThat(token).isEmpty();

	}

	@Test
	@DisplayName("PENDING 토큰을 ACTIVE로 전환하면 SortedSet에서 제거되고 Set에 추가된다.")
	void token_activate_success() {
		// arrange
		User user = new User("kimbro", "asdf");
		Concert concert = new Concert("아이유 10주년 콘서트");
		Schedule schedule = new Schedule(concert, LocalDate.now(), LocalDateTime.now());
		String uuid = "uuid_1";

		userJpaRepository.save(user);
		concertJpaRepository.save(concert);
		scheduleJpaRepository.save(schedule);

		Token token = Token.create(user, schedule, uuid, TokenStatus.PENDING);
		tokenRedisRepository.save(token);

		// act
		token.activate(1L, 0L, LocalDateTime.now().plusMinutes(10));
		tokenRedisRepository.saveActiveToken(token);

		// assert
		String pendingKey = "pending:schedule:" + schedule.getId();
		String activeKey = "active:schedule:" + schedule.getId();
		Set<String> pendingTokens = stringRedisTemplate.opsForZSet().range(pendingKey, 0, -1);
		Set<String> activeTokens = stringRedisTemplate.opsForSet().members(activeKey);

		assertThat(pendingTokens).isEmpty();
		assertThat(activeTokens).containsExactly(uuid);
	}

	@Test
	@DisplayName("ACTIVE 토큰 수를 정확히 조회할 수 있다.")
	void count_active_tokens() {
		// arrange
		User user = new User("kimbro", "asdf");
		Concert concert = new Concert("아이유 10주년 콘서트");
		Schedule schedule = new Schedule(concert, LocalDate.now(), LocalDateTime.now());

		userJpaRepository.save(user);
		concertJpaRepository.save(concert);
		scheduleJpaRepository.save(schedule);

		Token token1 = Token.create(user, schedule, "uuid_1", TokenStatus.ACTIVE);
		Token token2 = Token.create(user, schedule, "uuid_2", TokenStatus.ACTIVE);

		tokenRedisRepository.save(token1);
		tokenRedisRepository.saveActiveToken(token1);

		tokenRedisRepository.save(token2);
		tokenRedisRepository.saveActiveToken(token2);

		// act
		Long count = tokenRedisRepository.countActiveToken(schedule.getId());

		// assert
		assertThat(count).isEqualTo(2L);
	}

	@Test
	@DisplayName("스케줄 ID로 활성화된 토큰 UUID Set을 조회할 수 있다.")
	void find_active_tokens_by_scheduleId() {
		// arrange
		User user = new User("kimbro", "asdf");
		Concert concert = new Concert("아이유 콘서트");
		Schedule schedule = new Schedule(concert, LocalDate.now(), LocalDateTime.now());

		userJpaRepository.save(user);
		concertJpaRepository.save(concert);
		scheduleJpaRepository.save(schedule);

		Token token1 = Token.create(user, schedule, "uuid_1", TokenStatus.ACTIVE);
		Token token2 = Token.create(user, schedule, "uuid_2", TokenStatus.ACTIVE);

		tokenRedisRepository.save(token1);
		tokenRedisRepository.save(token2);

		// 활성 토큰으로 전환
		tokenRedisRepository.saveActiveToken(token1);
		tokenRedisRepository.saveActiveToken(token2);

		// act
		Set<String> result = tokenRedisRepository.findActiveTokens(schedule.getId());

		// assert
		Long count = tokenRedisRepository.countActiveToken(schedule.getId());
		assertThat(result).containsExactlyInAnyOrder("uuid_1", "uuid_2");
		assertThat(count).isEqualTo(2L);
	}

	@Test
	@DisplayName("active 토큰 삭제 시 Set과 String 양쪽에서 제거된다")
	void delete_active_token() {
		// arrange
		User user = new User("kimbro", "asdf");
		Concert concert = new Concert("아이유 콘서트");
		Schedule schedule = new Schedule(concert, LocalDate.now(), LocalDateTime.now());
		String uuid = "uuid_1";

		userJpaRepository.save(user);
		concertJpaRepository.save(concert);
		scheduleJpaRepository.save(schedule);

		Token token = Token.create(user, schedule, uuid, TokenStatus.ACTIVE);
		tokenRedisRepository.save(token);
		tokenRedisRepository.saveActiveToken(token);

		String setKey = "active:schedule:" + schedule.getId();
		String stringKey = "token:data:" + uuid;
		assertThat(stringRedisTemplate.opsForSet().isMember(setKey, uuid)).isTrue();
		assertThat(stringRedisTemplate.hasKey(stringKey)).isTrue();

		// act
		tokenRedisRepository.deleteActiveToken(schedule.getId(), uuid);

		// assert
		assertThat(stringRedisTemplate.opsForSet().isMember(setKey, uuid)).isFalse();
		assertThat(stringRedisTemplate.hasKey(stringKey)).isFalse();
	}
}
