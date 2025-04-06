package kr.hhplus.be.server.controller.balance;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@Tag(name = "Balance API", description = "잔액 충전 관련 API")
public interface BalanceControllerDocs {

	@Operation(summary = "잔액 충전", description = "사용자의 잔액을 충전합니다.")
	@ApiResponse(responseCode = "200", description = "잔액 충전 성공",
		content = @Content(mediaType = "application/json", schema = @Schema(implementation = ChargeResponse.class)))
	@PutMapping("/api/v1/balance/charge")
	ResponseEntity<ChargeResponse> charge(
		@RequestBody(description = "충전 요청 정보", required = true) ChargeRequest request
	);

	@Operation(summary = "잔액 조회", description = "사용자의 현재 잔액을 조회합니다.")
	@ApiResponse(responseCode = "200", description = "잔액 조회 성공",
		content = @Content(mediaType = "application/json", schema = @Schema(implementation = ChargeResponse.class)))
	@GetMapping("/api/v1/balance/{user_id}")
	ResponseEntity<ChargeResponse> getBalance(
		@Parameter(description = "조회할 사용자 ID", example = "12345", required = true)
		@PathVariable("user_id") Long userId
	);
}
