package kr.hhplus.be.server.infras.concert;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import kr.hhplus.be.server.domain.concert.Seat;

public interface SeatJpaRepository extends JpaRepository<Seat, Long> {
	List<Seat> findAllByScheduleId(Long scheduleId);

	@Lock(LockModeType.OPTIMISTIC)
	@Query("SELECT s FROM Seat s WHERE s.id = :id")
	Optional<Seat> findByIdWithLock(@Param("id") Long id);
}
