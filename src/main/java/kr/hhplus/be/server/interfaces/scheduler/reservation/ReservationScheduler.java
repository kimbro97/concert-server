package kr.hhplus.be.server.interfaces.scheduler.reservation;

import java.time.LocalDateTime;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import kr.hhplus.be.server.service.reservation.ReservationService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ReservationScheduler {

	private final ReservationService reservationService;

	@Scheduled(cron = "0 */1 * * * *")
	public void reservationCancel() {
		reservationService.cancel(LocalDateTime.now());
	}
}
