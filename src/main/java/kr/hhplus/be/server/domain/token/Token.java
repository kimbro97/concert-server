package kr.hhplus.be.server.domain.token;

import static lombok.AccessLevel.*;

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
	@JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "schedule_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	private Schedule schedule;

	private String uuid;

	@Enumerated(EnumType.STRING)
	private TokenStatus status;

	private LocalDateTime expireAt;

	@Builder
	private Token(User user, Schedule schedule, String uuid, TokenStatus status, LocalDateTime expireAt) {
		this.user = user;
		this.schedule = schedule;
		this.uuid = uuid;
		this.status = status;
		this.expireAt = expireAt;
	}

	public static Token create(User user, Schedule schedule, String uuid, TokenStatus status) {
		return Token.builder()
			.user(user)
			.schedule(schedule)
			.uuid(uuid)
			.status(status)
			.expireAt(null)
			.build();
	}

	public boolean isActive() {
		return status == TokenStatus.ACTIVE;
	}

	public void activate(Long location, Long activeCount, LocalDateTime expireAt) {
		if (location == 1L && activeCount < MAX_ACTIVE) {
			this.status = TokenStatus.ACTIVE;
			this.expireAt = expireAt;
		}
	}
}
