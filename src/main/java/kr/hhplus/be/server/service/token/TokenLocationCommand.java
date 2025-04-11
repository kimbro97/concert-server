package kr.hhplus.be.server.service.token;

import lombok.Getter;

@Getter
public class TokenLocationCommand {
	private String uuid;

	public TokenLocationCommand(String uuid) {
		this.uuid = uuid;
	}
}
