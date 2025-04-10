package kr.hhplus.be.server.interfaces.concert;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.service.concert.ConcertInfo;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@Tag(name = "Concert API", description = "콘서트 관련 API")
public interface ConcertControllerDocs {

	@Operation(summary = "콘서트 일정 조회", description = "특정 콘서트의 일정 목록을 조회합니다.")
	@ApiResponse(responseCode = "200", description = "콘서트 일정 조회 성공",
		content = @Content(mediaType = "application/json"))
	@GetMapping("/api/v1/concert/{concert_id}/schedule")
	kr.hhplus.be.server.interfaces.common.ApiResponse<List<ConcertInfo.ScheduleInfo>> getConcertSchedules(
		@Parameter(description = "조회할 콘서트의 ID")
		@PathVariable("concert_id") Long concertId,

		@ModelAttribute ConcertRequest.ConcertSchedule request
	);

	@Operation(summary = "콘서트 좌석 조회", description = "특정 콘서트 일정의 좌석 정보를 조회합니다.")
	@ApiResponse(responseCode = "200", description = "콘서트 좌석 조회 성공",
		content = @Content(mediaType = "application/json"))
	@GetMapping("/api/v1/schedule/{schedule_id}/seat")
	kr.hhplus.be.server.interfaces.common.ApiResponse<List<ConcertInfo.SeatInfo>> getSeat(
		@Parameter(description = "조회할 콘서트 일정의 ID")
		@PathVariable("schedule_id") Long scheduleId
	);
}
