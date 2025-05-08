package kr.hhplus.be.server.support.lock;

import kr.hhplus.be.server.support.exception.BusinessException;
import kr.hhplus.be.server.support.lock.strategy.LockStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class DistributedLockAspectTest {

	private LockStrategyFactory lockStrategyFactory;
	private LockStrategy lockStrategy;
	private DistributedService distributedService;

	@BeforeEach
	void setUp() {
		lockStrategyFactory = mock(LockStrategyFactory.class);
		lockStrategy = mock(LockStrategy.class);
		when(lockStrategyFactory.getStrategy(any())).thenReturn(lockStrategy);

		DistributedLockAspect aspect = new DistributedLockAspect(lockStrategyFactory);

		AspectJProxyFactory factory = new AspectJProxyFactory(new DistributedService());
		factory.addAspect(aspect);
		distributedService = factory.getProxy();
	}

	@Test
	@DisplayName("락 획득에 성공하면 비즈니스로직이 정상적으로 실행되고 락이 해제된다")
	void success() {
		// arrange
		when(lockStrategy.tryLock(anyString(), anyString(), anyLong(), anyLong(), any(TimeUnit.class)))
			.thenReturn(true);

		// act
		String result = distributedService.lock("abc");

		// assert
		assertThat(result).isEqualTo("success");
		verify(lockStrategy).unLock(startsWith("lock:"), anyString());
	}

	@Test
	@DisplayName("락 획득에 실패하면 예외가 발생하고 이후 로직은 실행되지 않는다.")
	void fail_lock() {
		// arrange
		when(lockStrategy.tryLock(anyString(), anyString(), anyLong(), anyLong(), any(TimeUnit.class)))
			.thenReturn(false);

		// act & assert
		assertThatThrownBy(() -> distributedService.lock("abc"))
			.isInstanceOf(BusinessException.class)
			.hasMessageContaining("락 획득에 실패했습니다.");

		verify(lockStrategy, never()).unLock(anyString(), anyString());
	}

	@Test
	@DisplayName("락을 획득하고 비즈니스 로직 실행시 예외가 발생하면 락은 해제된다.")
	void fail_business() {
		// arrange
		when(lockStrategy.tryLock(anyString(), anyString(), anyLong(), anyLong(), any(TimeUnit.class)))
			.thenReturn(true);

		// act & assert
		assertThatThrownBy(() -> distributedService.lockException("abc"))
			.isInstanceOf(RuntimeException.class)
			.hasMessage("비즈니스 실패");

		verify(lockStrategy).unLock(startsWith("lock:"), anyString());
	}
}
