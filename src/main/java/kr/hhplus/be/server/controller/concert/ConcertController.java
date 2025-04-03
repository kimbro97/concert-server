package kr.hhplus.be.server.controller.concert;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConcertController implements ConcertControllerDocs {

	@GetMapping("/api/v1/concert/{concert_id}/schedule")
	public ResponseEntity<List<ConcertScheduleResponse>> getConcertSchedules(
		@PathVariable("concert_id") Long concertId,
		@RequestHeader("WAITING_TOKEN") String token
	) {
		List<ConcertScheduleResponse> concertScheduleResponses = List.of(
			new ConcertScheduleResponse(LocalDate.of(2025, 1, 11)),
			new ConcertScheduleResponse(LocalDate.of(2025, 1, 12))
		);
		return ResponseEntity.ok(concertScheduleResponses);
	}

	@GetMapping("/api/v1/concert/{concert_id}/schedule/{concert_schedule_id}/seat")
	public ResponseEntity<List<ConcertScheduleSeatResponse>> getSeat(
		@PathVariable("concert_id") Long concertId,
		@PathVariable("concert_schedule_id") Long concertScheduleId,
		@RequestHeader("WAITING_TOKEN") String token
	) {
		List<ConcertScheduleSeatResponse> list = new ArrayList<>();
		for (int i = 1; i <= 50; i++) {
			list.add(new ConcertScheduleSeatResponse(i, Boolean.TRUE));
		}
		return ResponseEntity.ok(list);
	}
}
