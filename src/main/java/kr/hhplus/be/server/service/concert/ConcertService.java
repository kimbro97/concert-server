package kr.hhplus.be.server.service.concert;

import static kr.hhplus.be.server.support.exception.BusinessError.*;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.hhplus.be.server.domain.concert.ConcertRepository;
import kr.hhplus.be.server.domain.concert.Schedule;
import kr.hhplus.be.server.domain.concert.Seat;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ConcertService {

	private final ConcertRepository concertRepository;

	@Transactional(readOnly = true)
	@Cacheable(
		cacheNames = "seats",
		cacheManager = "scheduleCacheManager",
		key = "#command.concertId + ':' + #command.startData + ':' + #command.endData"
	)
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

	public List<ConcertInfo.SeatInfo> getSeat(ConcertCommand.Seat command) {

		concertRepository.findScheduleById(command.getScheduleId()).orElseThrow(NOT_FOUND_SCHEDULE_ERROR::exception);

		List<Seat> seats = concertRepository.findAllSeatByScheduleId(command.getScheduleId());

		return seats.stream()
			.map(ConcertInfo.SeatInfo::from)
			.toList();
	}
}
