package kr.hhplus.be.server.infras.concert;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
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

	public Long incrementScheduleCount(Long concertId, Long scheduleId, LocalDateTime today, LocalDate expireDate) {
		String key = "concert:" + concertId + ":schedule:" + scheduleId + ":count";
		Long count = stringRedisTemplate.opsForValue().increment(key);

		LocalDateTime targetMidnight = expireDate.plusDays(1).atStartOfDay();

		setExpireUntil(key, Duration.between(today, targetMidnight));
		return count;
	}

	public void addRanking(LocalDateTime today, Long concertId, double score) {
		String key = "concert:ranking:" + today.format(DateTimeFormatter.BASIC_ISO_DATE);
		zSetOps.add(key, String.valueOf(concertId), score);
		LocalDateTime targetMidnight = today.toLocalDate().plusDays(2).atStartOfDay();
		setExpireUntil(key, Duration.between(today, targetMidnight));
	}

	public List<Long> getTopRankings(LocalDateTime today) {
		String key = "concert:ranking:" + today.format(DateTimeFormatter.BASIC_ISO_DATE);
		Set<String> rankings = zSetOps.range(key, 0, 14);

		if (rankings.isEmpty()) {
			return List.of();
		}

		return rankings.stream()
			.map(Long::valueOf)
			.toList();
	}

	private void setExpireUntil(String key, Duration duration) {
		stringRedisTemplate.expire(key, duration);
	}
}
