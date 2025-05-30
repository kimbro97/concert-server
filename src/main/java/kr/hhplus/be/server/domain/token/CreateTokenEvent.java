package kr.hhplus.be.server.domain.token;

import lombok.Builder;
import lombok.Getter;

@Getter
public class CreateTokenEvent {

	private Long userId;
	private Long scheduleId;
	private String uuid;

	@Builder
	private CreateTokenEvent(Long userId, Long scheduleId, String uuid) {
		this.userId = userId;
		this.scheduleId = scheduleId;
		this.uuid = uuid;
	}

	public static CreateTokenEvent of(Token token) {
		return CreateTokenEvent.builder()
			.userId(token.getUser().getId())
			.scheduleId(token.getSchedule().getId())
			.uuid(token.getUuid())
			.build();
	}
}
