package kr.hhplus.be.server.domain.token;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import kr.hhplus.be.server.domain.concert.Schedule;

public interface TokenRepository {

	Optional<Token> findByUuid(String uuid);

	Token save(Token token);

	Long findTokenLocation(Long scheduleId, String uuid, TokenStatus status);

	Long countByScheduleIdAndStatus(Long scheduleId, TokenStatus status);

	void deleteByUuid(String uuid);

	List<Token> findAllByScheduleIdAndStatusOrderByCreatedAtAsc(Long scheduleId, TokenStatus tokenStatus);

	List<Token> findAllByScheduleIdAndStatusAndExpireAtBefore(Schedule schedule, TokenStatus tokenStatus, LocalDateTime now);

	void deleteAll(List<Token> tokens);
}
