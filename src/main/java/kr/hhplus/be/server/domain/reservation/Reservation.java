package kr.hhplus.be.server.domain.reservation;

import static jakarta.persistence.EnumType.*;
import static jakarta.persistence.GenerationType.*;
import static lombok.AccessLevel.*;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import kr.hhplus.be.server.domain.BaseEntity;
import kr.hhplus.be.server.domain.concert.Schedule;
import kr.hhplus.be.server.domain.concert.Seat;
import kr.hhplus.be.server.domain.user.User;
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
	@JoinColumn(name = "user_id")
	private User user;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "seat_id")
	private Seat seat;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "schedule_id")
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

	public void reserve() {
		seat.reserve();
		this.totalAmount = seat.calculatePrice();
		this.status = ReservationStatus.RESERVED;
		this.expiredAt = LocalDateTime.now().plusMinutes(5);
	}

	public static Reservation create(User user, Schedule schedule, Seat seat) {
		return Reservation.builder()
			.user(user)
			.schedule(schedule)
			.seat(seat)
			.build();
	}

}
