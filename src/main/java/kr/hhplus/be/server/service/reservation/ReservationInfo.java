package kr.hhplus.be.server.service.reservation;

import java.time.LocalDateTime;

import kr.hhplus.be.server.domain.reservation.Reservation;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ReservationInfo {
	private Long reservationId;
	private Long userId;
	private Long scheduleId;
	private Long seatId;
	private Long totalAmount;
	private String status;
	private LocalDateTime expiredAt;

	@Builder
	private ReservationInfo(Long reservationId, Long userId, Long scheduleId, Long seatId, Long totalAmount, String status, LocalDateTime expiredAt) {
		this.reservationId = reservationId;
		this.userId = userId;
		this.scheduleId = scheduleId;
		this.seatId = seatId;
		this.totalAmount = totalAmount;
		this.status = status;
		this.expiredAt = expiredAt;
	}

	public static ReservationInfo from(Reservation reservation) {

		return ReservationInfo.builder()
			.reservationId(reservation.getId())
			.userId(reservation.getUser().getId())
			.scheduleId(reservation.getSchedule().getId())
			.seatId(reservation.getSeat().getId())
			.totalAmount(reservation.getTotalAmount())
			.status(reservation.getStatus().toString())
			.expiredAt(reservation.getExpiredAt())
			.build();
	}
}
