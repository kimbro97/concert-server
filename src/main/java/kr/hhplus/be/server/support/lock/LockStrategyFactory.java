package kr.hhplus.be.server.support.lock;

import java.util.Map;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.support.lock.strategy.LockStrategy;
import kr.hhplus.be.server.support.lock.strategy.SimpleLockStrategy;

@Component
public class LockStrategyFactory {

	private final Map<LockType, LockStrategy> strategies;

	public LockStrategyFactory(SimpleLockStrategy simple) {
		this.strategies = Map.of(
			LockType.SIMPLE, simple
		);
	}

	public LockStrategy getStrategy(LockType lockType) {
		return strategies.get(lockType);
	}
}
