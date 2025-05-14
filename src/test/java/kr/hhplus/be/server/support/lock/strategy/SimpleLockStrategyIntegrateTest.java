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
class SimpleLockStrategyIntegrateTest {

	@AfterEach
	void tearDown() {
		stringRedisTemplate.delete("lock");
	}

	@Autowired
	private StringRedisTemplate stringRedisTemplate;

	@Autowired
	private SimpleLockStrategy simpleLockStrategy;

	@Test
	@DisplayName("tryLock 메서드로 락을 잡을 수 있다.")
	void try_lock() {
		// arrange
		String key = "lock";
		String value = "test";
		long waitTime = 5;
		long leaseTime = 300;
		TimeUnit timeUnit = TimeUnit.SECONDS;
		// act
		boolean success = simpleLockStrategy.tryLock(key, value, waitTime, leaseTime, timeUnit);
		// assert
		String saveLock = stringRedisTemplate.opsForValue().get(key);
		assertThat(success).isTrue();
		assertThat(saveLock).isEqualTo(value);
	}

	@Test
	@DisplayName("락을 잡고있는동안에는 락을 획득할 수 없다.")
	void try_lock_fail() {
		// arrange
		String key = "lock";
		String value = "test";
		long waitTime = 5;
		long leaseTime = 300;
		TimeUnit timeUnit = TimeUnit.SECONDS;
		stringRedisTemplate.opsForValue().setIfAbsent(key, value, leaseTime, timeUnit);

		// act
		boolean success = simpleLockStrategy.tryLock(key, value, waitTime, leaseTime, timeUnit);
		// assert
		assertThat(success).isFalse();
	}

	@Test
	@DisplayName("락을 해제할 수 있다.")
	void un_lock() {
		// arrange
		String key = "lock";
		String value = "test";
		long leaseTime = 300;
		TimeUnit timeUnit = TimeUnit.SECONDS;
		stringRedisTemplate.opsForValue().setIfAbsent(key, value, leaseTime, timeUnit);
		// act
		simpleLockStrategy.unLock(key, value);
		// assert
		String result = stringRedisTemplate.opsForValue().get(key);
		assertThat(result).isEqualTo(null);
	}

}
