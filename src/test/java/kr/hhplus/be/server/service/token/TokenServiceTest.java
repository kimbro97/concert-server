package kr.hhplus.be.server.service.token;

import static kr.hhplus.be.server.support.exception.BusinessError.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
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
	void createToken_성공() {
		// arrange
		Long userId = 1L;
		Long scheduleId = 10L;
		TokenCommand command = new TokenCommand(userId, scheduleId);

		User user = new User("testUser", "1234");
		Concert concert = new Concert("방탄콘서트");
		Schedule schedule = new Schedule(concert, LocalDate.now());

		when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		when(concertRepository.findScheduleById(scheduleId)).thenReturn(Optional.of(schedule));

		Token mockToken = Token.create(user, schedule, "uuid-123", TokenStatus.PENDING);
		when(tokenRepository.save(any(Token.class))).thenReturn(mockToken);

		// act
		TokenInfo result = tokenService.createToken(command);

		// assert
		assertThat(result).isNotNull();
		assertThat(result.getTokenValue()).isEqualTo("uuid-123");

		verify(userRepository).findById(userId);
		verify(concertRepository).findScheduleById(scheduleId);
		verify(tokenRepository).save(any(Token.class));
	}
	@Test
	@DisplayName("존재하지 않는 유저 ID로 토큰 생성 시 예외가 발생한다")
	void createToken_실패_유저없음() {
		// arrange
		Long userId = 1L;
		Long scheduleId = 10L;
		TokenCommand command = new TokenCommand(userId, scheduleId);

		when(userRepository.findById(userId)).thenReturn(Optional.empty());

		// act & assert
		assertThatThrownBy(() -> tokenService.createToken(command))
			.isInstanceOf(BusinessException.class);

		// 이후 로직이 실행되지 않는지 검증
		verify(concertRepository, never()).findScheduleById(any());
		verify(tokenRepository, never()).save(any());
	}

	@Test
	@DisplayName("존재하지 않는 스케줄 ID로 토큰 생성 시 예외가 발생한다")
	void createToken_실패_스케줄없음() {
		// arrange
		Long userId = 1L;
		Long scheduleId = 10L;
		TokenCommand command = new TokenCommand(userId, scheduleId);

		User user = new User("tester", "pw");
		when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		when(concertRepository.findScheduleById(scheduleId)).thenReturn(Optional.empty());

		// act & assert
		assertThatThrownBy(() -> tokenService.createToken(command))
			.isInstanceOf(BusinessException.class);

		// token 저장이 실행되지 않아야 함
		verify(tokenRepository, never()).save(any());
	}

	@Test
	@DisplayName("토큰 위치 조회 시 조건을 만족하면 상태가 ACTIVE로 변경되고 정보가 반환된다")
	void getTokenLocation_성공() {
		// arrange
		String uuid = "uuid-1234";
		Long scheduleId = 1L;
		Long location = 1L;
		Long activeCount = 999L;

		Schedule schedule = mock(Schedule.class);
		when(schedule.getId()).thenReturn(scheduleId);

		Token token = Token.create(new User("test", "1234"), schedule, uuid, TokenStatus.PENDING);

		when(tokenRepository.findByUuid(uuid)).thenReturn(Optional.of(token));
		when(tokenRepository.findTokenLocation(scheduleId, uuid, TokenStatus.PENDING)).thenReturn(location);
		when(tokenRepository.countByScheduleIdAndStatus(scheduleId, TokenStatus.ACTIVE)).thenReturn(activeCount);

		// act
		TokenLocationInfo info = tokenService.getTokenLocation(new TokenLocationCommand(uuid));

		// assert
		assertThat(info).isNotNull();
		assertThat(info.getStatus()).isEqualTo("ACTIVE");
		assertThat(info.getScheduleId()).isEqualTo(scheduleId);
		assertThat(info.getLocation()).isEqualTo(location);
	}

	@Test
	@DisplayName("uuid로 토큰을 찾을 수 없으면 예외가 발생한다")
	void getTokenLocation_토큰없음() {
		// arrange
		String uuid = "invalid-uuid";
		when(tokenRepository.findByUuid(uuid)).thenReturn(Optional.empty());

		// act & assert
		assertThatThrownBy(() -> tokenService.getTokenLocation(new TokenLocationCommand(uuid)))
			.isInstanceOf(BusinessException.class);
	}

	@Test
	@DisplayName("location이 1이 아니거나 activeCount가 최대이면 상태를 변경하지 않는다")
	void getTokenLocation_조건불충족_상태변경안함() {
		// arrange
		String uuid = "uuid-5678";
		Long scheduleId = 1L;
		Long location = 2L;
		Long activeCount = 1000L;

		Schedule schedule = mock(Schedule.class);
		when(schedule.getId()).thenReturn(scheduleId);

		Token token = Token.create(new User("user", "pass"), schedule, uuid, TokenStatus.PENDING);

		when(tokenRepository.findByUuid(uuid)).thenReturn(Optional.of(token));
		when(tokenRepository.findTokenLocation(scheduleId, uuid, TokenStatus.PENDING)).thenReturn(location);
		when(tokenRepository.countByScheduleIdAndStatus(scheduleId, TokenStatus.ACTIVE)).thenReturn(activeCount);

		// act
		TokenLocationInfo info = tokenService.getTokenLocation(new TokenLocationCommand(uuid));

		// assert
		assertThat(info.getStatus()).isEqualTo("PENDING");
	}
}
