package kr.hhplus.be.server.interfaces.balance;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BalanceController implements BalanceControllerDocs {

	@PutMapping("/api/v1/balance/charge")
	public ResponseEntity<ChargeResponse> charge(
		@RequestBody ChargeRequest request
	) {
		return ResponseEntity.ok(new ChargeResponse(10000L));
	}

	@GetMapping("/api/v1/balance/{user_id}")
	public ResponseEntity<ChargeResponse> getBalance(
		@PathVariable Long user_id
	) {
		return ResponseEntity.ok(new ChargeResponse(10000L));
	}
}
