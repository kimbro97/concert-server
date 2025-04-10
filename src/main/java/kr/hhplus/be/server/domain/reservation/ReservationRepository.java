package kr.hhplus.be.server.domain.reservation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository {
	Reservation save(Reservation reservation);
	Optional<Reservation> findById(Long id);

	List<Reservation> findAllByStatusAndExpiredAtBefore(ReservationStatus reservationStatus, LocalDateTime now);
}
