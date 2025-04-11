package kr.hhplus.be.server.domain.token;

import java.util.Optional;

public interface TokenRepository {

	Optional<Token> findByUuid(String uuid);

	Token save(Token token);

	Long findTokenLocation(Long scheduleId, String uuid, TokenStatus pending);

	Long countByScheduleIdAndStatus(Long scheduleId, TokenStatus status);

}
