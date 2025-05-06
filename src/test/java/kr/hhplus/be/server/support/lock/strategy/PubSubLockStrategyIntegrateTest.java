package kr.hhplus.be.server.support.lock.strategy;

import static org.assertj.core.api.Assertions.*;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PubSubLockStrategyIntegrateTest {

	@Autowired
	private PubSubLockStrategy pubSubLockStrategy;

	@Autowired
	private RedissonClient redissonClient;

	private final String key = "lock:test";

	@AfterEach
	void tearDown() {
		redissonClient.getKeys().delete(key);
	}

	@Test
	@DisplayName("락을 획득할 수 있다")
	void tryLock_success() {
		// arrange
		String value = "unique-value";

		// act
		boolean result = pubSubLockStrategy.tryLock(key, value, 3, 5, TimeUnit.SECONDS);

		// assert
		assertThat(result).isTrue();
	}

	@Test
	@DisplayName("다른 스레드가 이미 락을 잡고 있으면 락 획득에 실패한다")
	void tryLock_fails() throws InterruptedException {
		// arrange
		String value1 = "unique-value";
		String value2 = "unique-value";

		// 먼저 락을 잡고 있는 스레드
		Thread lockerThread = new Thread(() -> {
			pubSubLockStrategy.tryLock(key, value1, 1, 5, TimeUnit.SECONDS);
			try {
				Thread.sleep(2000); // 2초 동안 락 유지
			} catch (InterruptedException ignored) {}
		});
		lockerThread.start();
		Thread.sleep(500); // 락이 먼저 잡히도록 대기

		// 락 획득 시도 결과 저장용
		AtomicBoolean result = new AtomicBoolean(true);

		// 락을 획득하려는 다른 스레드
		Thread otherThread = new Thread(() -> {
			boolean success = pubSubLockStrategy.tryLock(key, value2, 1, 1, TimeUnit.SECONDS);
			result.set(success); // 기대값: false
		});

		// act
		otherThread.start();
		otherThread.join(); // 결과 기다림
		lockerThread.join();

		// assert
		assertThat(result.get()).isFalse();
	}

	@Test
	@DisplayName("락을 해제하면 잠금이 풀린다")
	void unlock_success() {
		// arrange
		pubSubLockStrategy.tryLock(key, "val", 1, 10, TimeUnit.SECONDS);

		// act
		pubSubLockStrategy.unLock(key, "val");

		// assert
		boolean available = redissonClient.getLock(key).tryLock();
		assertThat(available).isTrue();
		redissonClient.getLock(key).unlock(); // 정리
	}
}
