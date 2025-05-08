package kr.hhplus.be.server.support.lock.strategy;

import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PubSubLockStrategy implements LockStrategy {

	private final RedissonClient redissonClient;

	@Override
	public boolean tryLock(String key, String value, long waitTime, long leaseTime, TimeUnit timeUnit) {
		RLock lock = redissonClient.getLock(key);
		try {
			return lock.tryLock(waitTime, leaseTime, timeUnit);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			return false;
		}
	}

	@Override
	public void unLock(String key, String value) {
		RLock lock = redissonClient.getLock(key);
		if (lock.isHeldByCurrentThread()) {
			lock.unlock();
		}
	}
}
