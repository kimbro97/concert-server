package kr.hhplus.be.server.controller.token;

public class WaitingTokenResponse {
	private final Long location;

	public WaitingTokenResponse(Long location) {
		this.location = location;
	}

	public Long getLocation() {
		return location;
	}
}
