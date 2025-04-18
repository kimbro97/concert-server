package kr.hhplus.be.server.interfaces.api.reservation;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import kr.hhplus.be.server.interfaces.api.common.ApiResponse;
import kr.hhplus.be.server.service.reservation.ReservationInfo;
import kr.hhplus.be.server.service.reservation.ReservationService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ReservationController implements ReservationControllerDocs {

	private final ReservationService reservationService;

	@PostMapping("/api/v1/reservation")
	public ApiResponse<ReservationInfo> reserve(@RequestBody ReservationRequest request) {
		return ApiResponse.CREATE(reservationService.reserve(request.toCommand()));
	}
}
