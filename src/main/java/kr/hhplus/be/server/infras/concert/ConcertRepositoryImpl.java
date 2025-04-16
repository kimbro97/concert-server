package kr.hhplus.be.server.infras.concert;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import kr.hhplus.be.server.domain.concert.Concert;
import kr.hhplus.be.server.domain.concert.ConcertRepository;
import kr.hhplus.be.server.domain.concert.Schedule;
import kr.hhplus.be.server.domain.concert.Seat;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ConcertRepositoryImpl implements ConcertRepository {

	private final SeatJpaRepository seatJpaRepository;
	private final ConcertJpaRepository concertJpaRepository;
	private final ScheduleJpaRepository scheduleJpaRepository;

	@Override
	public Optional<Concert> findConcertById(Long concertId) {
		return concertJpaRepository.findById(concertId);
	}

	@Override
	public Optional<Schedule> findScheduleById(Long scheduleId) {
		return scheduleJpaRepository.findById(scheduleId);
	}

	@Override
	public Optional<Seat> findSeatById(Long SeatId) {
		return seatJpaRepository.findById(SeatId);
	}

	@Override
	public List<Seat> findAllSeatByScheduleId(Long scheduleId) {
		return seatJpaRepository.findAllByScheduleId(scheduleId);
	}

	@Override
	public List<Schedule> findAllByConcertIdAndDateBetween(Long concertId, LocalDate startDate, LocalDate endDate) {
		return scheduleJpaRepository.findAllByConcertIdAndDateBetween(concertId, startDate, endDate);
	}

	@Override
	public List<Schedule> findAllSchedule() {
		return scheduleJpaRepository.findAll();
	}
}
