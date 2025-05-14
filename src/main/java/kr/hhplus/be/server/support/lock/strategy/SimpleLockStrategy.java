package kr.hhplus.be.server.support.lock.strategy;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SimpleLockStrategy implements LockStrategy {

	private final StringRedisTemplate stringRedisTemplate;

	@Override
	public boolean tryLock(String key, String value, long waitTime, long leaseTime, TimeUnit timeUnit) {
		return Boolean.TRUE.equals(
			stringRedisTemplate.opsForValue()
				.setIfAbsent(key, value, leaseTime, timeUnit));
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
