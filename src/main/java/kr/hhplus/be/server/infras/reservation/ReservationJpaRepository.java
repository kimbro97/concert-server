package kr.hhplus.be.server.infras.reservation;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.hhplus.be.server.domain.reservation.Reservation;
import kr.hhplus.be.server.domain.reservation.ReservationStatus;

public interface ReservationJpaRepository extends JpaRepository<Reservation, Long> {
	List<Reservation> findAllByStatusAndExpiredAtBefore(ReservationStatus status, LocalDateTime now);
}
