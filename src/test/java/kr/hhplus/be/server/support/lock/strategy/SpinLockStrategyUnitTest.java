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
class SpinLockStrategyUnitTest {

	@Mock
	private StringRedisTemplate stringRedisTemplate;

	@Mock
	private ValueOperations<String, String> valueOps;

	@InjectMocks
	private SpinLockStrategy strategy;

	@Test
	@DisplayName("락이 바로 성공되면 true 반환")
	void tryLock_immediate_success() {
		// arrange
		when(stringRedisTemplate.opsForValue()).thenReturn(valueOps);
		when(valueOps.setIfAbsent(anyString(), anyString(), anyLong(), any()))
			.thenReturn(true);

		// act
		boolean result = strategy.tryLock("key", "val", 1, 1, TimeUnit.SECONDS);

		// assert
		assertThat(result).isTrue();
	}

	@Test
	@DisplayName("락이 재시도 끝에 성공되면 true 반환")
	void tryLock_retry_then_success() {
		// arrange
		when(stringRedisTemplate.opsForValue()).thenReturn(valueOps);
		when(valueOps.setIfAbsent(anyString(), anyString(), anyLong(), any()))
			.thenReturn(false, false, true);

		// act
		boolean result = strategy.tryLock("key", "val", 3, 1, TimeUnit.SECONDS);

		// assert
		assertThat(result).isTrue();
	}

	@Test
	@DisplayName("최대 재시도 실패하면 false 반환")
	void tryLock_fail_after_retries() {
		// arrange
		when(stringRedisTemplate.opsForValue()).thenReturn(valueOps);
		when(valueOps.setIfAbsent(anyString(), anyString(), anyLong(), any()))
			.thenReturn(false); // 3초 반복

		// act
		boolean result = strategy.tryLock("key", "val", 3, 1, TimeUnit.SECONDS);

		// assert
		assertThat(result).isFalse();
	}

	@Test
	@DisplayName("인터럽트 발생 시 false 반환")
	void tryLock_interrupted_exception() throws InterruptedException {
		// arrange
		when(stringRedisTemplate.opsForValue()).thenReturn(valueOps);
		when(valueOps.setIfAbsent(anyString(), anyString(), anyLong(), any()))
			.then(invocation -> {
				Thread.currentThread().interrupt();
				return false;
			});

		// act
		boolean result = strategy.tryLock("key", "val", 2, 1, TimeUnit.SECONDS);

		// assert
		assertThat(result).isFalse();
	}

	@Test
	@DisplayName("unlock 시 Lua 스크립트가 실행된다")
	void unlock_should_run_script() {
		// act
		strategy.unLock("lockKey", "value");

		// assert
		verify(stringRedisTemplate).execute(
			any(RedisScript.class),
			eq(List.of("lockKey")),
			eq("value")
		);
	}
}
