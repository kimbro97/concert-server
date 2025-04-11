package kr.hhplus.be.server.interfaces.api.token;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.hhplus.be.server.interfaces.api.common.ApiResponse;
import kr.hhplus.be.server.service.token.TokenInfo;
import kr.hhplus.be.server.service.token.TokenLocationCommand;
import kr.hhplus.be.server.service.token.TokenLocationInfo;
import kr.hhplus.be.server.service.token.TokenService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/waiting-token")
public class TokenController implements TokenControllerDocs {

	private final TokenService tokenService;

	@PostMapping
	public ApiResponse<TokenInfo> createToken(@RequestBody TokenRequest tokenRequest) {
		return ApiResponse.CREATE(tokenService.createToken(tokenRequest.toCommand()));
	}

	@GetMapping
	public ApiResponse<TokenLocationInfo> getToken(@RequestHeader("WAITING-TOKEN") String token) {
		return ApiResponse.OK(tokenService.getTokenLocation(new TokenLocationCommand(token)));
	}
}
