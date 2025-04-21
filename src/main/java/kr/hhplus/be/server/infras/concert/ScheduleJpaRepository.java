package kr.hhplus.be.server.infras.concert;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.hhplus.be.server.domain.concert.Schedule;

public interface ScheduleJpaRepository extends JpaRepository<Schedule, Long> {
	List<Schedule> findAllByConcertIdAndDateBetween(Long concertId, LocalDate startDate, LocalDate endDate);
}
