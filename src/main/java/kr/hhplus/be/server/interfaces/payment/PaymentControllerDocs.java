package kr.hhplus.be.server.interfaces.payment;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Payment API", description = "결제 관련 API")
public interface PaymentControllerDocs {

	@Operation(summary = "결제 요청", description = "예약 정보를 기반으로 결제를 진행합니다.")
	@ApiResponse(responseCode = "201", description = "결제 성공",
		content = @Content(mediaType = "application/json", schema = @Schema(implementation = PaymentResponse.class)))
	@PostMapping("/api/v1/payment")
	ResponseEntity<PaymentResponse> pay(
		@Parameter(description = "결제 요청 정보", required = true)
		@RequestBody PaymentRequest request
	);
}
