package kr.hhplus.be.server.infras.concert;

import static org.assertj.core.api.Assertions.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

@SpringBootTest
class ConcertRedisRepositoryTest {

	@Autowired
	private ConcertRedisRepository concertRedisRepository;

	@Autowired
	private StringRedisTemplate redisTemplate;

	@BeforeEach
	void setUp() {
		Set<String> keys = redisTemplate.keys("concert:1:schedule:*:count");
		if (keys != null && !keys.isEmpty()) {
			redisTemplate.delete(keys);
		}
		Set<String> sortedSet = redisTemplate.keys("concert:ranking:*");
		if (sortedSet != null && !sortedSet.isEmpty()) {
			redisTemplate.delete(sortedSet);
		}
	}

	@Test
	@DisplayName("concertId와 scheduleId를 받아서 key를 만들고 count를 1씩 증가시킬 수 있다.")
	void redis_reserve_count () {
	    // arrange
		Long concertId = 1L;
		Long scheduleId = 2L;
		// act
		Long count1 = concertRedisRepository.incrementScheduleCount(concertId, scheduleId);
		Long count2 = concertRedisRepository.incrementScheduleCount(concertId, scheduleId);
	    // assert
		Long totalCount = Long.valueOf(
			redisTemplate.opsForValue().get("concert:" + concertId + ":schedule:" + scheduleId + ":count")
		);
		assertThat(totalCount).isEqualTo(2L);
		assertThat(count1).isEqualTo(1);
		assertThat(count2).isEqualTo(2);
	}

	@Test
	@DisplayName("concertId와 score를 받아서 sorted set 자료구조를 만들 수 있다.")
	void redis_reserve_add_ranking () {
		// arrange
		Long concertId = 1L;
		LocalDateTime today = LocalDateTime.now();
		LocalDateTime openedAt = LocalDateTime.now().minusMinutes(5);
		long elapsedMillis = Duration.between(openedAt, today).toMillis();

		// act
		concertRedisRepository.addRanking(today, concertId, elapsedMillis);
		// assert
		Long size = redisTemplate.opsForZSet()
			.size("concert:ranking:" + today.format(DateTimeFormatter.BASIC_ISO_DATE));
		assertThat(size).isEqualTo(1);
	}

	@Test
	@DisplayName("매진까지 걸린 시간이 짧은 순으로 인기 concert 랭킹을 조회할 수 있다")
	void find_concert_ranking () {
		// arrange
		LocalDateTime today = LocalDateTime.now();

		Long concertId1 = 1L;
		LocalDateTime openedAt1 = LocalDateTime.now().minusMinutes(5);
		long elapsedMillis1 = Duration.between(openedAt1, today).toMillis();
		concertRedisRepository.addRanking(today, concertId1, elapsedMillis1);

		Long concertId2 = 2L;
		LocalDateTime openedAt2 = LocalDateTime.now().minusMinutes(4);
		long elapsedMillis2 = Duration.between(openedAt2, today).toMillis();
		concertRedisRepository.addRanking(today, concertId2, elapsedMillis2);

		Long concertId3 = 3L;
		LocalDateTime openedAt3 = LocalDateTime.now().minusMinutes(6);
		long elapsedMillis3 = Duration.between(openedAt3, today).toMillis();
		concertRedisRepository.addRanking(today, concertId3, elapsedMillis3);
		// act

		List<Long> topRankingList = concertRedisRepository.getTopRankings(today);
		// assert
		assertThat(topRankingList).hasSize(3);
		assertThat(topRankingList.get(0)).isEqualTo(2L);
		assertThat(topRankingList.get(1)).isEqualTo(1L);
		assertThat(topRankingList.get(2)).isEqualTo(3L);
	}
}
