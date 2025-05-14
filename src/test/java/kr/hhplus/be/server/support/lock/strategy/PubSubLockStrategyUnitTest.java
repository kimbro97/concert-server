package kr.hhplus.be.server.support.lock.strategy;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

@ExtendWith(MockitoExtension.class)
class PubSubLockStrategyUnitTest {

	@Mock
	private RLock rLock;

	@Mock
	private RedissonClient redissonClient;

	@InjectMocks
	private PubSubLockStrategy pubSubLockStrategy;

	@Test
	@DisplayName("tryLock 성공 시 true 반환")
	void tryLock_success() throws InterruptedException {
		// arrange
		when(redissonClient.getLock("key")).thenReturn(rLock);
		when(rLock.tryLock(3, 5, TimeUnit.SECONDS)).thenReturn(true);

		// act
		boolean result = pubSubLockStrategy.tryLock("key", "value", 3, 5, TimeUnit.SECONDS);

		// assert
		assertThat(result).isTrue();
		verify(rLock).tryLock(3, 5, TimeUnit.SECONDS);
	}

	@Test
	@DisplayName("tryLock 인터럽트 발생 시 false 반환")
	void tryLock_interrupted() throws InterruptedException {
		// arrange
		when(redissonClient.getLock("key")).thenReturn(rLock);
		when(rLock.tryLock(3, 5, TimeUnit.SECONDS)).thenThrow(new InterruptedException());

		// act
		boolean result = pubSubLockStrategy.tryLock("key", "value", 3, 5, TimeUnit.SECONDS);

		// assert
		assertThat(result).isFalse();
		verify(rLock).tryLock(3, 5, TimeUnit.SECONDS);
	}

	@Test
	@DisplayName("현재 스레드가 락을 가지고 있으면 unlock 수행")
	void unLock_success() {
		// arrange
		when(redissonClient.getLock("key")).thenReturn(rLock);
		when(rLock.isHeldByCurrentThread()).thenReturn(true);

		// act
		pubSubLockStrategy.unLock("key", "value");

		// assert
		verify(rLock).unlock();
	}

	@Test
	@DisplayName("현재 스레드가 락을 가지고 있지 않으면 unlock 하지 않음")
	void unLock_notHeldByCurrentThread() {
		// arrange
		when(redissonClient.getLock("key")).thenReturn(rLock);
		when(rLock.isHeldByCurrentThread()).thenReturn(false);

		// act
		pubSubLockStrategy.unLock("key", "value");

		// assert
		verify(rLock, never()).unlock();
	}
}
