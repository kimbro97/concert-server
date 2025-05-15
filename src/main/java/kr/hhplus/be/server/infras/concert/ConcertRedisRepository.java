package kr.hhplus.be.server.infras.concert;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

@Repository
public class ConcertRedisRepository {

	private final StringRedisTemplate stringRedisTemplate;
	private final ZSetOperations<String, String> zSetOps;

	public ConcertRedisRepository(StringRedisTemplate stringRedisTemplate) {
		this.stringRedisTemplate = stringRedisTemplate;
		this.zSetOps = stringRedisTemplate.opsForZSet();
	}

	public Long incrementScheduleCount(Long concertId, Long scheduleId) {
		String key = "concert:" + concertId + ":schedule:" + scheduleId + ":count";
		return stringRedisTemplate.opsForValue().increment(key);
	}

	public void addRanking(LocalDateTime today, Long concertId, double score) {
		String key = "concert:ranking:" + today.format(DateTimeFormatter.BASIC_ISO_DATE);
		zSetOps.add(key, String.valueOf(concertId), score);
	}

}
