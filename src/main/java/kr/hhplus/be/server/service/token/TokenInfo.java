package kr.hhplus.be.server.service.token;

import kr.hhplus.be.server.domain.token.Token;
import lombok.Builder;
import lombok.Getter;

@Getter
public class TokenInfo {
	private Long tokenId;
	private String tokenValue;

	@Builder
	private TokenInfo(Long tokenId, String tokenValue) {
		this.tokenId = tokenId;
		this.tokenValue = tokenValue;
	}

	public static TokenInfo from(Token token) {
		return TokenInfo.builder()
			.tokenId(token.getId())
			.tokenValue(token.getUuid())
			.build();
	}
}
