package kr.hhplus.be.server.infras.token;

import java.util.Map;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import kr.hhplus.be.server.domain.token.Token;

@Repository
public class TokenRedisRepository {

	private final StringRedisTemplate stringRedisTemplate;
	private final ZSetOperations<String, String> zSetOperations;
	private final HashOperations<String, String, String> hashOperations;


	public TokenRedisRepository(StringRedisTemplate stringRedisTemplate) {
		this.stringRedisTemplate = stringRedisTemplate;
		this.zSetOperations = stringRedisTemplate.opsForZSet();
		this.hashOperations = stringRedisTemplate.opsForHash();
	}

	public Token save(Token token) {
		// ZSet에 저장 (순번 관리용)
		String key = "pending:schedule:" + token.getSchedule().getId();
		zSetOperations.add(key, token.getUuid(), System.currentTimeMillis());
		// Hash에 저장 (uuid → 상세 정보)
		String hashKey = "token:data";
		hashOperations.putAll(hashKey + ":" + token.getUuid(), Map.of(
			"userId", String.valueOf(token.getUser().getId()),
			"scheduleId", String.valueOf(token.getSchedule().getId()),
			"status", token.getStatus().name()
		));
		return token;
	}
}

