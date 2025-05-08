package kr.hhplus.be.server.support.lock.strategy;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SpinLockStrategy implements LockStrategy {

	private static final int RETRY_INTERVAL_MS = 100;

	private final StringRedisTemplate stringRedisTemplate;

	@Override
	public boolean tryLock(String key, String value, long waitTime, long leaseTime, TimeUnit timeUnit) {

		long waitTimeInMillis = timeUnit.toMillis(waitTime);
		long end = System.currentTimeMillis() + waitTimeInMillis;

		while (System.currentTimeMillis() < end) {
			Boolean success = stringRedisTemplate.opsForValue()
				.setIfAbsent(key, value, leaseTime, timeUnit);

			if (Boolean.TRUE.equals(success)) {
				return true;
			}

			try {
				Thread.sleep(RETRY_INTERVAL_MS);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				return false;
			}
		}

		return false;
	}

	@Override
	public void unLock(String key, String value) {
		String script = """
			if redis.call('get', KEYS[1]) == ARGV[1] then
				return redis.call('del', KEYS[1])
			else
				return 0
			end
		""";
		stringRedisTemplate.execute(RedisScript.of(script, Long.class), List.of(key), value);
	}
}
