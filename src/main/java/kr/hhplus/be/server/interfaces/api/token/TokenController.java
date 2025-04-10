package kr.hhplus.be.server.interfaces.api.token;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/waiting-token")
public class TokenController implements TokenControllerDocs {

	@PostMapping
	public ResponseEntity<TokenResponse> createToken(
		@RequestBody TokenRequest tokenRequest
	) {
		return ResponseEntity.status(201)
			.body(new TokenResponse("UUID"));
	}

	@GetMapping
	public ResponseEntity<WaitingTokenResponse> getToken(@RequestHeader("WAITING-TOKEN") String token) {
		return ResponseEntity.ok(new WaitingTokenResponse(1L));
	}
}
