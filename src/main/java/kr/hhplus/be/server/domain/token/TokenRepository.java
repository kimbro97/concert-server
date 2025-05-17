package kr.hhplus.be.server.domain.token;

import java.util.Optional;
import java.util.Set;

public interface TokenRepository {

	Optional<Token> findByUuid(String uuid);

	Token save(Token token);

	Long findTokenLocation(Long scheduleId, String uuid, TokenStatus status);

	Optional<Token> findFirstPendingToken(Long scheduleId);

	Long countActiveToken(Long scheduleId);

	void saveActiveToken(Token token);

	Set<String> findActiveTokens(Long scheduleId);

	void deleteActiveToken(Long scheduleId, String uuid);
}
