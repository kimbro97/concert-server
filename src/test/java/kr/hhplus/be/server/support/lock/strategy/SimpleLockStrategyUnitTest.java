package kr.hhplus.be.server.support.lock.strategy;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.RedisScript;

@ExtendWith(MockitoExtension.class)
class SimpleLockStrategyUnitTest {

	@Mock
	private StringRedisTemplate stringRedisTemplate;

	@InjectMocks
	private SimpleLockStrategy simpleLockStrategy;

	@Test
	@DisplayName("락을 획득할 수 있으면 true를 반환한다.")
	void lock_success() {
		// arrange
		long waitTime = 5;
		long leaseTime = 5;
		TimeUnit timeUnit = TimeUnit.SECONDS;

		ValueOperations<String, String> valueOps = mock(ValueOperations.class);
		when(stringRedisTemplate.opsForValue()).thenReturn(valueOps);
		when(valueOps.setIfAbsent("lock:1", "value", leaseTime, timeUnit)).thenReturn(true);

		// act & assert
		boolean success = simpleLockStrategy.tryLock("lock:1", "value", waitTime, leaseTime, timeUnit);

		// assert
		assertThat(success).isTrue();
	}

	@Test
	@DisplayName("락을 획득하지 못하면 false를 반환한다.")
	void lock_fail() {
		// arrange
		long waitTime = 5;
		long leaseTime = 5;
		TimeUnit timeUnit = TimeUnit.SECONDS;

		ValueOperations<String, String> valueOps = mock(ValueOperations.class);
		when(stringRedisTemplate.opsForValue()).thenReturn(valueOps);
		when(valueOps.setIfAbsent("lock:1", "value", leaseTime, timeUnit)).thenReturn(false);

		// act & assert
		boolean success = simpleLockStrategy.tryLock("lock:1", "value", waitTime, leaseTime, timeUnit);

		// assert
		assertThat(success).isFalse();
	}

	@Test
	@DisplayName("unlock 시 Lua 스크립트가 실행된다")
	void unlock_executes_lua_script() {
		// arrange
		String key = "lock:1";
		String value = "value";

		// act
		simpleLockStrategy.unLock(key, value);

		// assert
		verify(stringRedisTemplate).execute(
			any(RedisScript.class),
			eq(List.of(key)),
			eq(value)
		);
	}
}
