package kr.hhplus.be.server.service.token;

import static kr.hhplus.be.server.domain.token.TokenStatus.*;
import static kr.hhplus.be.server.support.exception.BusinessError.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
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
			tokenRepository.findFirstPendingToken(schedule.getId())
				.ifPresent(token -> {
					Long activeCount = tokenRepository.countActiveToken(schedule.getId());

					if (activeCount < 1000) {
						token.activate(1L, activeCount, LocalDateTime.now().plusMinutes(10));
						tokenRepository.saveActiveToken(token);
					}
				});
		}
	}

	@Transactional
	public void expireToken(LocalDateTime now) {
		List<Schedule> schedules = concertRepository.findAllSchedule();

		for (Schedule schedule : schedules) {
			//schedules 기반으로 active set으로 조회해온다.
			Set<String> activeTokens = tokenRepository.findActiveTokens(schedule.getId());
			// set을 순회하면서 uuid값으로 string token을 조회한다.
			for (String uuid : activeTokens) {
				tokenRepository.findByUuid(uuid).ifPresent(token -> {
					// token 시간이 지났는지 검증한다.
					if (token.isExpired(now)) {
						// 자났다면 set과 string token을 삭제한다.
						tokenRepository.deleteActiveToken(schedule.getId(), uuid);
					}
				});
			}
		}
	}
}
