package kr.hhplus.be.server.domain.token;
import java.time.LocalDateTime;
import kr.hhplus.be.server.domain.concert.Schedule;
import kr.hhplus.be.server.domain.user.User;
import lombok.Builder;
import lombok.Getter;

@Getter
public class Token {

	private static final Long MAX_ACTIVE = 1000L;

	private Long id;
	private User user;
	private Schedule schedule;
	private String uuid;
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

	public boolean isExpired(LocalDateTime now) {
		return expireAt != null && expireAt.isBefore(now);
	}
}
