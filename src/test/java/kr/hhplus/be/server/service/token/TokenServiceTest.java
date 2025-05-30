package kr.hhplus.be.server.service.token;

import static kr.hhplus.be.server.domain.token.TokenStatus.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import kr.hhplus.be.server.domain.concert.Concert;
import kr.hhplus.be.server.domain.concert.ConcertRepository;
import kr.hhplus.be.server.domain.concert.Schedule;
import kr.hhplus.be.server.domain.token.Token;
import kr.hhplus.be.server.domain.token.TokenRepository;
import kr.hhplus.be.server.domain.token.TokenStatus;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import kr.hhplus.be.server.support.exception.BusinessException;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {
	@Mock UserRepository userRepository;

	@Mock ConcertRepository concertRepository;

	@Mock TokenRepository tokenRepository;

	@InjectMocks TokenService tokenService;

	@Test
	@DisplayName("토큰 생성 요청 시 UUID 기반의 PENDING 상태 토큰이 저장되고 반환된다")
	void createToken_success() {
		Long userId = 1L;
		Long scheduleId = 10L;
		TokenCommand command = new TokenCommand(userId, scheduleId);

		User user = new User("testUser", "1234");
		Schedule schedule = new Schedule(new Concert("concert"), LocalDate.now(), LocalDateTime.now());
		Token token = Token.create(user, schedule, "uuid-123", TokenStatus.PENDING);

		when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		when(concertRepository.findScheduleById(scheduleId)).thenReturn(Optional.of(schedule));
		when(tokenRepository.save(any(Token.class))).thenReturn(token);

		TokenInfo result = tokenService.createToken(command);

		assertThat(result.getTokenValue()).isEqualTo("uuid-123");
	}

	@Test
	@DisplayName("존재하지 않는 유저 ID로 토큰 생성 시 예외 발생")
	void createToken_fail_userNotFound() {
		TokenCommand command = new TokenCommand(1L, 2L);
		when(userRepository.findById(1L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> tokenService.createToken(command))
			.isInstanceOf(BusinessException.class);
	}

	@Test
	@DisplayName("존재하지 않는 스케줄 ID로 토큰 생성 시 예외 발생")
	void createToken_fail_scheduleNotFound() {
		User user = new User("testUser", "1234");
		TokenCommand command = new TokenCommand(1L, 2L);

		when(userRepository.findById(1L)).thenReturn(Optional.of(user));
		when(concertRepository.findScheduleById(2L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> tokenService.createToken(command))
			.isInstanceOf(BusinessException.class);
	}

	@Test
	@DisplayName("uuid로 위치 조회 시 순서를 반환한다")
	void getTokenLocation_success() {
		Schedule schedule = mock(Schedule.class);
		when(schedule.getId()).thenReturn(10L);

		Token token = Token.create(new User("testUser", "1234"), schedule, "uuid", PENDING);
		when(tokenRepository.findByUuid("uuid")).thenReturn(Optional.of(token));
		when(tokenRepository.findTokenLocation(10L, "uuid", PENDING)).thenReturn(5L);

		TokenLocationInfo info = tokenService.getTokenLocation(new TokenLocationCommand("uuid"));

		assertThat(info.getLocation()).isEqualTo(5L);
		assertThat(info.getScheduleId()).isEqualTo(10L);
		assertThat(info.getStatus()).isEqualTo(PENDING.name());
	}

	@Test
	@DisplayName("uuid로 조회한 토큰이 ACTIVE이면 위치는 1이다")
	void getTokenLocation_activeToken_returns1() {
		Schedule schedule = mock(Schedule.class);
		when(schedule.getId()).thenReturn(10L);

		Token token = Token.create(new User("testUser", "1234"), schedule, "uuid", ACTIVE);
		when(tokenRepository.findByUuid("uuid")).thenReturn(Optional.of(token));

		TokenLocationInfo info = tokenService.getTokenLocation(new TokenLocationCommand("uuid"));

		assertThat(info.getLocation()).isEqualTo(1L);
		assertThat(info.getStatus()).isEqualTo(ACTIVE.name());
	}

	@Test
	@DisplayName("토큰이 존재하지 않으면 예외 발생")
	void getTokenLocation_fail_tokenNotFound() {
		when(tokenRepository.findByUuid("not_exist")).thenReturn(Optional.empty());

		assertThatThrownBy(() -> tokenService.getTokenLocation(new TokenLocationCommand("not_exist")))
			.isInstanceOf(BusinessException.class);
	}

	@Test
	@DisplayName("활성화 대상 토큰이 있고 active 수가 1000 미만이면 토큰을 활성화하고 저장한다")
	void activateToken_success() {
		Schedule schedule = mock(Schedule.class);
		Token token = Token.create(new User("testUser", "1234"), schedule, "uuid", PENDING);

		when(tokenRepository.findFirstPendingToken(1L)).thenReturn(Optional.of(token));
		when(tokenRepository.countActiveToken(1L)).thenReturn(999L);

		tokenService.activateToken(1L);

		assertThat(token.getStatus()).isEqualTo(ACTIVE);
		verify(tokenRepository).saveActiveToken(token);
	}

	@Test
	@DisplayName("ACTIVE 토큰 수가 1000 이상이면 활성화되지 않는다")
	void activateToken_noActivationIfMaxReached() {
		Schedule schedule = mock(Schedule.class);
		Token token = Token.create(new User("testUser", "1234"), schedule, "uuid", PENDING);

		when(tokenRepository.findFirstPendingToken(1L)).thenReturn(Optional.of(token));
		when(tokenRepository.countActiveToken(1L)).thenReturn(1000L);

		tokenService.activateToken(1L);

		assertThat(token.getStatus()).isEqualTo(PENDING);
		verify(tokenRepository, never()).saveActiveToken(token);
	}

	@Test
	@DisplayName("만료된 active 토큰은 삭제된다")
	void expireToken_success() {
		// arrange
		Long scheduleId = 1L;
		String expiredUuid = "expired_token";
		String validUuid = "valid_token";

		Schedule schedule = mock(Schedule.class);
		when(schedule.getId()).thenReturn(scheduleId);

		List<Schedule> schedules = List.of(schedule);
		Set<String> uuids = Set.of(expiredUuid, validUuid);
		LocalDateTime now = LocalDateTime.now();

		Token expiredToken = mock(Token.class);
		Token validToken = mock(Token.class);

		when(expiredToken.isExpired(now)).thenReturn(true);
		when(validToken.isExpired(now)).thenReturn(false);

		when(concertRepository.findAllSchedule()).thenReturn(schedules);
		when(tokenRepository.findActiveTokens(scheduleId)).thenReturn(uuids);

		when(tokenRepository.findByUuid(expiredUuid)).thenReturn(Optional.of(expiredToken));
		when(tokenRepository.findByUuid(validUuid)).thenReturn(Optional.of(validToken));

		// act
		tokenService.expireToken(now);

		// assert
		verify(tokenRepository).deleteActiveToken(scheduleId, expiredUuid);
		verify(tokenRepository, never()).deleteActiveToken(scheduleId, validUuid);
	}

	@Test
	@DisplayName("UUID로 Token 조회에 실패하면 아무것도 하지 않는다")
	void expireToken_missingTokenData() {
		Schedule schedule = mock(Schedule.class);
		when(schedule.getId()).thenReturn(1L);

		when(concertRepository.findAllSchedule()).thenReturn(List.of(schedule));
		when(tokenRepository.findActiveTokens(1L)).thenReturn(Set.of("missing_uuid"));
		when(tokenRepository.findByUuid("missing_uuid")).thenReturn(Optional.empty());

		// act
		tokenService.expireToken(LocalDateTime.now());

		// assert
		verify(tokenRepository, never()).deleteActiveToken(any(), any());
	}
}
