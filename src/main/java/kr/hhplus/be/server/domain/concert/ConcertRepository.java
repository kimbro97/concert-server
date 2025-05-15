package kr.hhplus.be.server.domain.concert;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ConcertRepository {

	Optional<Concert> findConcertById(Long concertId);
	Optional<Schedule> findScheduleById(Long ScheduleId);
	Optional<Seat> findSeatById(Long SeatId);
	List<Seat> findAllSeatByScheduleId(Long scheduleId);
	List<Schedule> findAllByConcertIdAndDateBetween(Long concertId, LocalDate startDate, LocalDate endDate);

	List<Schedule> findAllSchedule();

	Seat saveSeatAndFlush(Seat seat);

	Long incrementScheduleCount(Long concertId, Long scheduleId);

	void addRanking(LocalDateTime today, Long concertId, double score);

}
