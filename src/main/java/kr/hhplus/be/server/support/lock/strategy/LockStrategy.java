package kr.hhplus.be.server.support.lock.strategy;

import java.util.concurrent.TimeUnit;

public interface LockStrategy {
	boolean tryLock(String key, String value, long waitTime, long leaseTime, TimeUnit timeUnit);
	void unLock(String key, String value);
}
