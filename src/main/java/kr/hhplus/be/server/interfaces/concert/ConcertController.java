package kr.hhplus.be.server.interfaces.concert;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import kr.hhplus.be.server.interfaces.common.ApiResponse;
import kr.hhplus.be.server.service.concert.ConcertCommand;
import kr.hhplus.be.server.service.concert.ConcertInfo;
import kr.hhplus.be.server.service.concert.ConcertService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ConcertController implements ConcertControllerDocs {

	private final ConcertService concertService;

	@GetMapping("/api/v1/concert/{concert_id}/schedule")
	public ApiResponse<List<ConcertInfo.ScheduleInfo>> getConcertSchedules(
		@PathVariable("concert_id") Long concertId,
		@ModelAttribute ConcertRequest.ConcertSchedule request
	) {
		return ApiResponse.OK(concertService.getSchedule(request.toCommand(concertId)));
	}

	@GetMapping("/api/v1/schedule/{schedule_id}/seat")
	public ApiResponse<List<ConcertInfo.SeatInfo>> getSeat(
		@PathVariable("schedule_id") Long scheduleId
	) {
		return ApiResponse.OK(concertService.getSeat(new ConcertCommand.Seat(scheduleId)));
	}
}
