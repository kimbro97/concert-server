package kr.hhplus.be.server.service.token;

import static kr.hhplus.be.server.support.exception.BusinessError.*;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.hhplus.be.server.domain.concert.ConcertRepository;
import kr.hhplus.be.server.domain.concert.Schedule;
import kr.hhplus.be.server.domain.token.Token;
import kr.hhplus.be.server.domain.token.TokenRepository;
import kr.hhplus.be.server.domain.token.TokenStatus;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import kr.hhplus.be.server.support.exception.BusinessError;
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
		Token token = Token.create(user, schedule, uuid, TokenStatus.PENDING);

		Token savedToken = tokenRepository.save(token);

		return TokenInfo.from(savedToken);
	}

	@Transactional
	public TokenLocationInfo getTokenLocation(TokenLocationCommand command) {

		Token token = tokenRepository.findByUuid(command.getUuid())
			.orElseThrow(NOT_FOUND_TOKEN_ERROR::exception);

		Long scheduleId = token.getSchedule().getId();

		Long location = tokenRepository.findTokenLocation(scheduleId, command.getUuid(), TokenStatus.PENDING);

		Long activeCount = tokenRepository.countByScheduleIdAndStatus(scheduleId, TokenStatus.ACTIVE);

		token.activateIfFirstAndAvailable(location, activeCount);

		return new TokenLocationInfo(scheduleId, location, token.getStatus());
	}
}
