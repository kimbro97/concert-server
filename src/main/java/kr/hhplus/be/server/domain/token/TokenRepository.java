package kr.hhplus.be.server.domain.token;

import java.util.List;
import java.util.Optional;

public interface TokenRepository {

	Optional<Token> findByUuid(String uuid);

	Token save(Token token);

	Long findTokenLocation(Long scheduleId, String uuid, TokenStatus status);

	Long countByScheduleIdAndStatus(Long scheduleId, TokenStatus status);

	void deleteByUuid(String uuid);

	List<Token> findAllByScheduleIdAndStatusOrderByCreatedAtAsc(Long scheduleId, TokenStatus tokenStatus);
}
