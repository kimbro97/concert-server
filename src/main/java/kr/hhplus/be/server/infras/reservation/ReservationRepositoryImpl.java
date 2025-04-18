package kr.hhplus.be.server.infras.reservation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import kr.hhplus.be.server.domain.reservation.Reservation;
import kr.hhplus.be.server.domain.reservation.ReservationRepository;
import kr.hhplus.be.server.domain.reservation.ReservationStatus;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ReservationRepositoryImpl implements ReservationRepository {

	private final ReservationJpaRepository reservationJpaRepository;

	@Override
	public Reservation save(Reservation reservation) {
		return reservationJpaRepository.save(reservation);
	}

	@Override
	public Optional<Reservation> findById(Long id) {
		return reservationJpaRepository.findById(id);
	}

	@Override
	public List<Reservation> findAllByStatusAndExpiredAtBefore(ReservationStatus reservationStatus, LocalDateTime now) {
		return reservationJpaRepository.findAllByStatusAndExpiredAtBefore(reservationStatus, now);
	}
}
