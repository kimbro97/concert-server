package kr.hhplus.be.server.support.lock;

import static kr.hhplus.be.server.support.lock.LockType.*;

import java.util.Map;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.support.lock.strategy.LockStrategy;
import kr.hhplus.be.server.support.lock.strategy.PubSubLockStrategy;
import kr.hhplus.be.server.support.lock.strategy.SimpleLockStrategy;
import kr.hhplus.be.server.support.lock.strategy.SpinLockStrategy;

@Component
public class LockStrategyFactory {

	private final Map<LockType, LockStrategy> strategies;

	public LockStrategyFactory(SimpleLockStrategy simple, SpinLockStrategy spin, PubSubLockStrategy pubSub) {
		this.strategies = Map.of(
			SIMPLE, simple,
			SPIN, spin,
			PUBSUB, pubSub
		);
	}

	public LockStrategy getStrategy(LockType lockType) {
		return strategies.get(lockType);
	}
}
