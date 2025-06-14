package kr.hhplus.be.server.service.concert;

import static kr.hhplus.be.server.support.exception.BusinessError.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.hhplus.be.server.domain.concert.Concert;
import kr.hhplus.be.server.domain.concert.ConcertRepository;
import kr.hhplus.be.server.domain.concert.Schedule;
import kr.hhplus.be.server.domain.concert.Seat;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConcertService {

	private final ConcertRepository concertRepository;

	@Transactional(readOnly = true)
	public List<ConcertInfo> getConcertTopRankings(LocalDateTime today) {
		List<Long> topRankings = concertRepository.getTopRankings(today);

		if (topRankings.isEmpty()) {
			topRankings = concertRepository.getTopRankings(today.minusDays(1));
		}

		Map<Long, Concert> concertMap = concertRepository.findAllByIdIn(topRankings).stream()
			.collect(Collectors.toMap(Concert::getId, Function.identity()));

		return topRankings.stream()
			.map(concertMap::get)
			.map(ConcertInfo::from)
			.toList();
	}

	@Transactional(readOnly = true)
	public List<ConcertInfo.ScheduleInfo> getSchedule(ConcertCommand.Schedule command) {

		concertRepository.findConcertById(command.getConcertId())
			.orElseThrow(NOT_FOUND_CONCERT_ERROR::exception);

		List<Schedule> schedules = concertRepository.findAllByConcertIdAndDateBetween(
			command.getConcertId(), command.getStartData(),
			command.getEndData());

		return schedules.stream()
			.map(ConcertInfo.ScheduleInfo::from)
			.toList();
	}

	@Transactional(readOnly = true)
	public List<ConcertInfo.SeatInfo> getSeat(ConcertCommand.Seat command) {

		concertRepository.findScheduleById(command.getScheduleId()).orElseThrow(NOT_FOUND_SCHEDULE_ERROR::exception);

		List<Seat> seats = concertRepository.findAllSeatByScheduleId(command.getScheduleId());

		return seats.stream()
			.map(ConcertInfo.SeatInfo::from)
			.toList();
	}

	@Transactional
	public void addRanking(ConcertCommand.AddRanking command) {
		try {

			Long count = concertRepository.incrementScheduleCount(command.getConcertId(), command.getScheduleId(), command.getToday(), command.getScheduleDate());
			Long seatCount = concertRepository.countByScheduleId(command.getScheduleId());

			if (count.equals(seatCount)) {
				concertRepository.addRanking(command.getToday(), command.getConcertId(), Duration.between(command.getOpenedAt(), command.getToday()).toMillis());
			}
		} catch (Exception e) {
			log.warn("콘서트 랭킹 갱신 중 예외 발생 - concertId: {}, scheduleId: {}, message: {}", command.getConcertId(), command.getScheduleId(), e.getMessage(), e);
		}
	}
}
