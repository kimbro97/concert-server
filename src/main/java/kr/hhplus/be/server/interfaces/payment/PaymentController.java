package kr.hhplus.be.server.interfaces.payment;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import kr.hhplus.be.server.interfaces.common.ApiResponse;

@RestController
public class PaymentController implements PaymentControllerDocs {

	@PostMapping
	public ResponseEntity<PaymentResponse> pay(
		@RequestBody PaymentRequest request
	) {
		PaymentResponse response = new PaymentResponse(
			1L, // paymentId
			100L, // reservationId
			50000L, // totalAmount
			LocalDateTime.now().minusDays(1), // createdAt
			LocalDateTime.now() // updatedAt
		);
		return ResponseEntity.status(201).body(response);
	}
}
