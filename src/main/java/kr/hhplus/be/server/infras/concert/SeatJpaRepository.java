package kr.hhplus.be.server.infras.concert;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.hhplus.be.server.domain.concert.Seat;

public interface SeatJpaRepository extends JpaRepository<Seat, Long> {
	List<Seat> findAllByScheduleId(Long scheduleId);
}
