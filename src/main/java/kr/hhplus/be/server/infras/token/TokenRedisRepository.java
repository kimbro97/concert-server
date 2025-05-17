package kr.hhplus.be.server.infras.token;

import static kr.hhplus.be.server.support.exception.BusinessError.*;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import kr.hhplus.be.server.domain.token.Token;
import kr.hhplus.be.server.support.exception.BusinessError;

@Repository
public class TokenRedisRepository {

	private final static String STRING_KEY_PREFIX = "token:data:";
	private final static String SET_KEY_PREFIX = "active:schedule:";
	private final static String SORTED_SET_KEY_PREFIX = "pending:schedule:";


	private final ObjectMapper objectMapper;
	private final StringRedisTemplate stringRedisTemplate;
	private final ZSetOperations<String, String> zSetOperations;
	private final SetOperations<String, String> setOperations;


	public TokenRedisRepository(StringRedisTemplate stringRedisTemplate, ObjectMapper objectMapper) {
		this.stringRedisTemplate = stringRedisTemplate;
		this.zSetOperations = stringRedisTemplate.opsForZSet();
		this.setOperations = stringRedisTemplate.opsForSet();
		this.objectMapper = objectMapper;
	}

	public Token save(Token token) {

		String sortedKey = SORTED_SET_KEY_PREFIX + token.getSchedule().getId();
		zSetOperations.add(sortedKey, token.getUuid(), System.currentTimeMillis());

		String stringKey = STRING_KEY_PREFIX + token.getUuid() ;
		try {
			String json = objectMapper.writeValueAsString(token);
			stringRedisTemplate.opsForValue().set(stringKey, json);
		} catch (JsonProcessingException e) {
			throw TOKEN_SERIALIZATION_ERROR.exception();
		}
		return token;
	}

	public Optional<Token> findByUuid(String uuid) {
		String stringKey = STRING_KEY_PREFIX + uuid ;
		String json = stringRedisTemplate.opsForValue().get(stringKey);

		if (json == null) {
			return Optional.empty();
		}

		Token token = null;
		try {
			token = objectMapper.readValue(json, Token.class);
		} catch (JsonProcessingException e) {
			throw TOKEN_DESERIALIZATION_ERROR.exception();
		}

		return Optional.of(token);
	}

	public Long findTokenLocation(Long scheduleId, String uuid) {
		String sortedKey = SORTED_SET_KEY_PREFIX + scheduleId;
		Long rank = zSetOperations.rank(sortedKey, uuid);
		return rank + 1;
	}

	public Optional<Token> findFirstPendingToken(Long scheduleId) {
		String sortedKey = SORTED_SET_KEY_PREFIX + scheduleId;
		Set<String> firstSet = zSetOperations.range(sortedKey, 0, 0);

		if (firstSet == null || firstSet.isEmpty()) {
			return Optional.empty();
		}

		String uuid = firstSet.iterator().next();
		String stringKey = STRING_KEY_PREFIX + uuid;
		String json = stringRedisTemplate.opsForValue().get(stringKey);

		if (json == null) {
			return Optional.empty();
		}

		try {
			Token token = objectMapper.readValue(json, Token.class);
			return Optional.of(token);
		} catch (JsonProcessingException e) {
			throw TOKEN_DESERIALIZATION_ERROR.exception();
		}
	}

	public void saveActiveToken(Token token) {
		String sortedKey = SORTED_SET_KEY_PREFIX + token.getSchedule().getId();
		zSetOperations.remove(sortedKey, token.getUuid());

		String setKey = SET_KEY_PREFIX + token.getSchedule().getId();
		setOperations.add(setKey, token.getUuid());

		String stringKey = STRING_KEY_PREFIX + token.getUuid();
		try {
			String json = objectMapper.writeValueAsString(token);
			stringRedisTemplate.opsForValue().set(stringKey, json);
		} catch (JsonProcessingException e) {
			throw TOKEN_SERIALIZATION_ERROR.exception();
		}
	}

	public Long countActiveToken(Long scheduleId) {
		String setKey = SET_KEY_PREFIX + scheduleId;
		return setOperations.size(setKey);
	}

	public Set<String> findActiveTokens(Long scheduleId) {
		String setKey = SET_KEY_PREFIX + scheduleId;
		return setOperations.members(setKey);
	}

	public void deleteActiveToken(Long scheduleId, String uuid) {
		String setKey = SET_KEY_PREFIX + scheduleId;
		String stringKey = STRING_KEY_PREFIX + uuid;

		setOperations.remove(setKey, uuid);

		stringRedisTemplate.delete(stringKey);
	}
}

