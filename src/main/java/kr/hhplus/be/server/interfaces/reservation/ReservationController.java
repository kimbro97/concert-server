package kr.hhplus.be.server.interfaces.reservation;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReservationController implements ReservationControllerDocs {
	@PostMapping("/api/v1/reservation")
	public ResponseEntity<ReservationResponse> reserve(
		@RequestBody ReservationRequest request
	) {
		ReservationResponse response = new ReservationResponse(
			1L,
			12345L,
			10L,
			25L,
			50000L,
			"CONFIRMED",
			LocalDateTime.now().minusDays(1),
			LocalDateTime.now()
		);
		return ResponseEntity.status(201).body(response);
	}
}
