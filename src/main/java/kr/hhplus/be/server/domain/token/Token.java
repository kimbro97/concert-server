package kr.hhplus.be.server.domain.token;

import static lombok.AccessLevel.*;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import kr.hhplus.be.server.domain.BaseEntity;
import kr.hhplus.be.server.domain.concert.Schedule;
import kr.hhplus.be.server.domain.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Token extends BaseEntity {

	private static final Long MAX_ACTIVE = 1000L;

	@Id
	@Column(name = "token_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "schedule_id")
	private Schedule schedule;

	private String uuid;

	@Enumerated(EnumType.STRING)
	private TokenStatus status;

	@Builder
	private Token(User user, Schedule schedule, String uuid, TokenStatus status) {
		this.user = user;
		this.schedule = schedule;
		this.uuid = uuid;
		this.status = status;
	}

	public static Token create(User user, Schedule schedule, String uuid, TokenStatus status) {
		return Token.builder()
			.user(user)
			.schedule(schedule)
			.uuid(uuid)
			.status(status)
			.build();
	}

	public void activateIfFirstAndAvailable(Long location, Long activeCount) {
		if (location == 1L && activeCount < MAX_ACTIVE) {
			this.status = TokenStatus.ACTIVE;
		}
	}
}
