package kr.hhplus.be.server.service.token;

import kr.hhplus.be.server.domain.token.TokenStatus;
import lombok.Getter;

@Getter
public class TokenLocationInfo {

	private Long scheduleId;
	private Long location;
	private String status;

	public TokenLocationInfo(Long scheduleId, Long location, TokenStatus status) {
		this.scheduleId = scheduleId;
		this.location = location;
		this.status = status.name();
	}
}
