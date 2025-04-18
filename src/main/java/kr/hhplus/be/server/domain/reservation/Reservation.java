package kr.hhplus.be.server.domain.reservation;

import static jakarta.persistence.EnumType.*;
import static jakarta.persistence.GenerationType.*;
import static lombok.AccessLevel.*;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import kr.hhplus.be.server.domain.BaseEntity;
import kr.hhplus.be.server.domain.concert.Schedule;
import kr.hhplus.be.server.domain.concert.Seat;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.support.exception.BusinessError;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Reservation extends BaseEntity {

	@Id
	@Column(name = "reservation_id")
	@GeneratedValue(strategy = IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	private User user;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "seat_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	private Seat seat;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "schedule_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	private Schedule schedule;

	private Long totalAmount;

	@Enumerated(STRING)
	private ReservationStatus status;

	private LocalDateTime expiredAt;

	@Builder
	private Reservation(User user, Schedule schedule, Seat seat) {
		this.user = user;
		this.schedule = schedule;
		this.seat = seat;
	}

	public void reserve(LocalDateTime expiredAt) {
		seat.reserve();
		this.totalAmount = seat.calculatePrice();
		this.status = ReservationStatus.RESERVED;
		this.expiredAt = expiredAt;
	}

	public void validateNotExpired(LocalDateTime now) {
		if (this.expiredAt.isBefore(now)) {
			throw BusinessError.EXPIRED_RESERVATION_ERROR.exception();
		}
		this.status = ReservationStatus.CONFIRMED;
	}

	public void cancel() {
		cancelValidate();
		this.status = ReservationStatus.CANCEL;
		this.seat.cancel();
	}

	public static Reservation create(User user, Schedule schedule, Seat seat) {
		return Reservation.builder()
			.user(user)
			.schedule(schedule)
			.seat(seat)
			.build();
	}

	private void cancelValidate() {
		if (this.status == ReservationStatus.CANCEL) {
			throw BusinessError.ALREADY_RESERVED_CANCEL_ERROR.exception();
		}
	}
}
