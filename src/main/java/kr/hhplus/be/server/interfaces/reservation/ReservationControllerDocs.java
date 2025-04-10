package kr.hhplus.be.server.interfaces.reservation;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.service.reservation.ReservationInfo;

@Tag(name = "Reservation API", description = "예약 관련 API")
public interface ReservationControllerDocs {

	@Operation(summary = "예약 생성", description = "새로운 예약을 생성합니다.")
	@ApiResponse(responseCode = "201", description = "예약 성공",
		content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReservationInfo.class)))
	@PostMapping("/api/v1/reservation")
	kr.hhplus.be.server.interfaces.common.ApiResponse<ReservationInfo> reserve(
		@Parameter(description = "예약 정보를 입력해주세요.")
		@RequestBody ReservationRequest request
	);
}
