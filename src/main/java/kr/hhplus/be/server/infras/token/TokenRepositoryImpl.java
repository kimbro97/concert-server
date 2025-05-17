package kr.hhplus.be.server.infras.token;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Repository;

import kr.hhplus.be.server.domain.token.Token;
import kr.hhplus.be.server.domain.token.TokenRepository;
import kr.hhplus.be.server.domain.token.TokenStatus;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class TokenRepositoryImpl implements TokenRepository {

	private final TokenRedisRepository tokenRedisRepository;

	@Override
	public Optional<Token> findByUuid(String uuid) {
		return tokenRedisRepository.findByUuid(uuid);
	}

	@Override
	public Token save(Token token) {
		return tokenRedisRepository.save(token);
	}

	@Override
	public Long findTokenLocation(Long scheduleId, String uuid, TokenStatus status) {
		return tokenRedisRepository.findTokenLocation(scheduleId, uuid);
	}

	@Override
	public Optional<Token> findFirstPendingToken(Long scheduleId) {
		return tokenRedisRepository.findFirstPendingToken(scheduleId);
	}

	@Override
	public Long countActiveToken(Long scheduleId) {
		return tokenRedisRepository.countActiveToken(scheduleId);
	}

	@Override
	public void saveActiveToken(Token token) {
		tokenRedisRepository.saveActiveToken(token);
	}

	@Override
	public Set<String> findActiveTokens(Long scheduleId) {
		return tokenRedisRepository.findActiveTokens(scheduleId);
	}

	@Override
	public void deleteActiveToken(Long scheduleId, String uuid) {
		tokenRedisRepository.deleteActiveToken(scheduleId, uuid);
	}

}
