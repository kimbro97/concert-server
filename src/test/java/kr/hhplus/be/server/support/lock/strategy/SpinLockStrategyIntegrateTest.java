package kr.hhplus.be.server.support.lock.strategy;

import static org.assertj.core.api.Assertions.*;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

@SpringBootTest
class SpinLockStrategyIntegrateTest {

	@Autowired
	private SpinLockStrategy spinLockStrategy;

	@Autowired
	private StringRedisTemplate stringRedisTemplate;

	@AfterEach
	void tearDown() {
		stringRedisTemplate.delete("lock:test");
	}

	@Test
	@DisplayName("락을 잡을 수 있으면 true 반환")
	void tryLock_success() {
		// arrange
		String key = "lock:test";
		String value = "test-value";
		// act
		boolean result = spinLockStrategy.tryLock(key, value, 3, 3, TimeUnit.SECONDS);

		// assert
		assertThat(result).isTrue();
		assertThat(stringRedisTemplate.opsForValue().get(key)).isEqualTo(value);
	}

	@Test
	@DisplayName("이미 락이 잡혀 있으면 재시도 끝에 false 반환")
	void tryLock_fail_due_to_existing_lock() {
		// arrange
		String key = "lock:test";
		String value = "test-value";
		stringRedisTemplate.opsForValue().setIfAbsent(key, "someone-else", 10, TimeUnit.SECONDS);

		// act
		boolean result = spinLockStrategy.tryLock(key, value, 3, 3, TimeUnit.SECONDS);

		// assert
		assertThat(result).isFalse();
		assertThat(stringRedisTemplate.opsForValue().get(key)).isEqualTo("someone-else");
	}

	@Test
	@DisplayName("락을 해제하면 Redis에서 키가 사라진다")
	void unlock_success() {
		// arrange
		String key = "lock:test";
		String value = "test-value";
		stringRedisTemplate.opsForValue().set(key, value);

		// act
		spinLockStrategy.unLock(key, value);

		// assert
		assertThat(stringRedisTemplate.opsForValue().get(key)).isNull();
	}
}
