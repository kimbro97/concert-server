package kr.hhplus.be.server.interfaces.scheduler.token;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import kr.hhplus.be.server.service.token.TokenService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TokenScheduler {

	private final TokenService tokenService;

	@Scheduled(fixedRate = 10000)
	public void activateToken() {
		tokenService.activateToken();
	}
}
