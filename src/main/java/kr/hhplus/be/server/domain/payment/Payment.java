package kr.hhplus.be.server.domain.payment;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import kr.hhplus.be.server.domain.BaseEntity;
import kr.hhplus.be.server.domain.balance.Balance;
import kr.hhplus.be.server.domain.reservation.Reservation;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.support.exception.BusinessError;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment extends BaseEntity {

	@Id
	@Column(name = "payment_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "reservation_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	private Reservation reservation;

	@Enumerated(EnumType.STRING)
	private PaymentStatus status;

	private LocalDateTime paidAt;

	private Payment(User user, Reservation reservation) {
		this.user = user;
		this.reservation = reservation;
	}

	public static Payment create(User user, Reservation reservation) {
		return new Payment(user, reservation);
	}

	public void pay(Balance balance, LocalDateTime now) {
		this.payValidate();
		reservation.validateNotExpired(now);
		balance.use(reservation.getTotalAmount());
		this.status = PaymentStatus.PAID;
		this.paidAt = now;
	}

	private void payValidate() {
		if (this.status == PaymentStatus.PAID) {
			throw BusinessError.ALREADY_PAID_ERROR.exception();
		}
	}
}
