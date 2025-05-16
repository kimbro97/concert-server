package kr.hhplus.be.server.domain.concert;

import static jakarta.persistence.GenerationType.*;
import static kr.hhplus.be.server.support.exception.BusinessError.*;
import static lombok.AccessLevel.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import kr.hhplus.be.server.domain.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Schedule extends BaseEntity {

	@Id
	@Column(name = "schedule_id")
	@GeneratedValue(strategy = IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "concert_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	private Concert concert;

	private LocalDate date;

	private LocalDateTime openedAt;

	public Schedule(Concert concert, LocalDate date, LocalDateTime openedAt) {
		this.concert = concert;
		this.date = date;
		this.openedAt = openedAt;
	}

	public void validateOpen(LocalDateTime now) {
		if (openedAt.isAfter(now)) {
			throw RESERVATION_NOT_ALLOWED_BEFORE_OPEN.exception();
		}
	}
}
