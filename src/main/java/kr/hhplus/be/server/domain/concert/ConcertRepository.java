package kr.hhplus.be.server.domain.concert;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ConcertRepository {

	Optional<Concert> findConcertById(Long concertId);
	Optional<Schedule> findScheduleById(Long ScheduleId);
	Optional<Seat> findSeatById(Long SeatId);
	List<Seat> findAllSeatByScheduleId(Long scheduleId);
	List<Schedule> findAllByConcertIdAndDateBetween(Long concertId, LocalDate startDate, LocalDate endDate);

}
