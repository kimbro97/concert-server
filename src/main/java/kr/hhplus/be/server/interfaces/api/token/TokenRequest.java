package kr.hhplus.be.server.interfaces.api.token;

import kr.hhplus.be.server.service.token.TokenCommand;
import lombok.Getter;

@Getter
public class TokenRequest {
	private Long userId;
	private Long scheduleId;

	public TokenRequest(Long userId, Long scheduleId) {
		this.userId = userId;
		this.scheduleId = scheduleId;
	}

	public TokenCommand toCommand() {
		return new TokenCommand(userId, scheduleId);
	}
}
