package kr.hhplus.be.server.interfaces.api.token;

public class TokenResponse {
	private final String token;

	public TokenResponse(String token) {
		this.token = token;
	}

	public String getToken() {
		return token;
	}
}
