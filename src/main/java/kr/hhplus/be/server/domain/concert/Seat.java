package kr.hhplus.be.server.domain.concert;

import static jakarta.persistence.FetchType.*;
import static jakarta.persistence.GenerationType.*;
import static lombok.AccessLevel.*;

import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import kr.hhplus.be.server.domain.BaseEntity;
import kr.hhplus.be.server.support.exception.BusinessError;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Seat extends BaseEntity {

	@Id
	@Column(name = "seat_id")
	@GeneratedValue(strategy = IDENTITY)
	private Long id;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "schedule_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	private Schedule schedule;

	private String number;

	private Long price;

	private Boolean isSelectable;

	public Seat(Schedule schedule, String number, Long price, Boolean isSelectable) {
		this.schedule = schedule;
		this.number = number;
		this.price = price;
		this.isSelectable = isSelectable;
	}

	public Long calculatePrice() {
		return this.price;
	}

	public void reserve() {
		validateSelectable();
		isSelectable = false;
	}

	public void cancel() {
		isSelectable = true;
	}

	private void validateSelectable() {
		if (Boolean.FALSE.equals(this.isSelectable)) {
			throw BusinessError.ALREADY_RESERVED_SEAT.exception();
		}
	}
}
