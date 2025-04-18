package kr.hhplus.be.server.service.reservation;

import static kr.hhplus.be.server.support.exception.BusinessError.*;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.hhplus.be.server.domain.concert.ConcertRepository;
import kr.hhplus.be.server.domain.concert.Schedule;
import kr.hhplus.be.server.domain.concert.Seat;
import kr.hhplus.be.server.domain.reservation.Reservation;
import kr.hhplus.be.server.domain.reservation.ReservationRepository;
import kr.hhplus.be.server.domain.reservation.ReservationStatus;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReservationService {

	private final UserRepository userRepository;
	private final ConcertRepository concertRepository;
	private final ReservationRepository reservationRepository;

	@Transactional
	public ReservationInfo reserve(ReservationCommand command) {

		User user = userRepository.findById(command.getUserId())
			.orElseThrow(NOT_FOUND_USER_ERROR::exception);

		Schedule schedule = concertRepository.findScheduleById(command.getScheduleId())
			.orElseThrow(NOT_FOUND_SCHEDULE_ERROR::exception);

		Seat seat = concertRepository.findSeatById(command.getSeatId())
			.orElseThrow(NOT_FOUND_SEAT_ERROR::exception);

		Reservation reservation = Reservation.create(user, schedule, seat);

		reservation.reserve(LocalDateTime.now().plusMinutes(5));

		Reservation savedReservation = reservationRepository.save(reservation);

		return ReservationInfo.from(savedReservation);
	}

	@Transactional
	public void cancel(LocalDateTime now) {
		List<Reservation> reservations = reservationRepository.findAllByStatusAndExpiredAtBefore(ReservationStatus.RESERVED, now);

		for (Reservation reservation : reservations) {
			reservation.cancel();
		}

	}
}
