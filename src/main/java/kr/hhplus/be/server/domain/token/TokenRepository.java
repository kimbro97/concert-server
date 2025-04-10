package kr.hhplus.be.server.domain.token;

public interface TokenRepository {
	Token findByUuid(String uuid);
	Token save(Token token);
}
