package kr.hhplus.be.server.interfaces.scheduler.token;

import java.time.LocalDateTime;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import kr.hhplus.be.server.service.token.TokenService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TokenScheduler {

	private final TokenService tokenService;

	// @Scheduled(fixedRate = 10000)
	// public void activateToken() {
	// 	tokenService.activateToken();
	// }

	@Scheduled(fixedRate = 10000)
	public void expireToken() {
		tokenService.expireToken(LocalDateTime.now());
	}
}
