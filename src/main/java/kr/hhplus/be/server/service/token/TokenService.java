package kr.hhplus.be.server.service.token;

import static kr.hhplus.be.server.domain.token.TokenStatus.*;
import static kr.hhplus.be.server.support.exception.BusinessError.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.hhplus.be.server.domain.concert.ConcertRepository;
import kr.hhplus.be.server.domain.concert.Schedule;
import kr.hhplus.be.server.domain.token.Token;
import kr.hhplus.be.server.domain.token.TokenRepository;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TokenService {

	private final UserRepository userRepository;
	private final TokenRepository tokenRepository;
	private final ConcertRepository concertRepository;

	@Transactional
	public TokenInfo createToken(TokenCommand command) {

		User user = userRepository.findById(command.getUserId())
			.orElseThrow(NOT_FOUND_USER_ERROR::exception);

		Schedule schedule = concertRepository.findScheduleById(command.getScheduleId())
			.orElseThrow(NOT_FOUND_SCHEDULE_ERROR::exception);

		String uuid = UUID.randomUUID().toString().replace("-", "");
		Token token = Token.create(user, schedule, uuid, PENDING);

		Token savedToken = tokenRepository.save(token);

		return TokenInfo.from(savedToken);
	}

	@Transactional(readOnly = true)
	public TokenLocationInfo getTokenLocation(TokenLocationCommand command) {

		Long location = 1L;

		Token token = tokenRepository.findByUuid(command.getUuid())
			.orElseThrow(NOT_FOUND_TOKEN_ERROR::exception);

		Long scheduleId = token.getSchedule().getId();

		if (token.isActive()) {
			return new TokenLocationInfo(scheduleId, location, token.getStatus());
		}

		location = tokenRepository.findTokenLocation(scheduleId, command.getUuid(), PENDING);

		return new TokenLocationInfo(scheduleId, location, token.getStatus());
	}

	@Transactional
	public void activateToken() {
		List<Schedule> schedules = concertRepository.findAllSchedule();

		for (Schedule schedule : schedules) {
			List<Token> tokens = tokenRepository.findAllByScheduleIdAndStatusOrderByCreatedAtAsc(
				schedule.getId(), PENDING);
			Long activeCount = tokenRepository.countByScheduleIdAndStatus(schedule.getId(), ACTIVE);

			if (!tokens.isEmpty() && activeCount < 1000) {
				Token first = tokens.get(0);
				first.activate(1L, activeCount, LocalDateTime.now().plusMinutes(10));
				tokenRepository.save(first);
			}
		}
	}

	@Transactional
	public void expireToken(LocalDateTime now) {
		List<Schedule> schedules = concertRepository.findAllSchedule();

		for (Schedule schedule : schedules) {
			List<Token> tokens = tokenRepository.findAllByScheduleIdAndStatusAndExpireAtBefore(schedule, ACTIVE,
				now);
			tokenRepository.deleteAll(tokens);
		}
	}
}
