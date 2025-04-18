package kr.hhplus.be.server.infras.token;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import kr.hhplus.be.server.domain.concert.Schedule;
import kr.hhplus.be.server.domain.token.Token;
import kr.hhplus.be.server.domain.token.TokenRepository;
import kr.hhplus.be.server.domain.token.TokenStatus;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class TokenRepositoryImpl implements TokenRepository {

	private final TokenJpaRepository tokenJpaRepository;

	@Override
	public Optional<Token> findByUuid(String uuid) {
		return tokenJpaRepository.findByUuid(uuid);
	}

	@Override
	public Token save(Token token) {
		return tokenJpaRepository.save(token);
	}

	@Override
	public Long findTokenLocation(Long scheduleId, String uuid, TokenStatus status) {
		return tokenJpaRepository.findTokenLocation(scheduleId, uuid, status) + 1L;
	}

	@Override
	public Long countByScheduleIdAndStatus(Long scheduleId, TokenStatus status) {
		return tokenJpaRepository.countByScheduleIdAndStatus(scheduleId, status);
	}

	@Override
	public void deleteByUuid(String uuid) {
		tokenJpaRepository.deleteByUuid(uuid);
	}

	@Override
	public List<Token> findAllByScheduleIdAndStatusOrderByCreatedAtAsc(Long scheduleId, TokenStatus tokenStatus) {
		return tokenJpaRepository.findAllByScheduleIdAndStatusOrderByCreatedAtAsc(scheduleId, tokenStatus);
	}

	@Override
	public List<Token> findAllByScheduleIdAndStatusAndExpireAtBefore(Schedule schedule, TokenStatus tokenStatus,
		LocalDateTime now) {
		return tokenJpaRepository.findAllByScheduleIdAndStatusAndExpireAtBefore(schedule.getId(), tokenStatus, now);
	}

	@Override
	public void deleteAll(List<Token> tokens) {
		tokenJpaRepository.deleteAll(tokens);
	}
}
