package kr.hhplus.be.server.service.token;

import lombok.Getter;

@Getter
public class TokenCommand {

	private Long userId;
	private Long scheduleId;

	public TokenCommand(Long userId, Long scheduleId) {
		this.userId = userId;
		this.scheduleId = scheduleId;
	}
}
