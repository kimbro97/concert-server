package kr.hhplus.be.server.domain.concert;

import static jakarta.persistence.FetchType.*;
import static jakarta.persistence.GenerationType.*;
import static lombok.AccessLevel.*;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
public class Seat extends BaseEntity {

	@Id
	@Column(name = "seat_id")
	@GeneratedValue(strategy = IDENTITY)
	private Long id;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "schedule_id")
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
}
