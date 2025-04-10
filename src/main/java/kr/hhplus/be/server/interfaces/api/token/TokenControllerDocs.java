package kr.hhplus.be.server.interfaces.api.token;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.service.token.TokenInfo;

@Tag(name = "Waiting Token API", description = "대기열 토큰 관련 API")
public interface TokenControllerDocs {

	@Operation(summary = "토큰 생성", description = "새로운 대기열 토큰을 생성합니다.")
	@ApiResponse(responseCode = "201", description = "토큰 생성 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TokenInfo.class)))
	@PostMapping
	kr.hhplus.be.server.interfaces.api.common.ApiResponse<TokenInfo> createToken(
		@RequestBody TokenRequest tokenRequest
	);

	@Operation(summary = "토큰 조회", description = "대기열 토큰을 조회합니다.")
	@ApiResponse(responseCode = "200", description = "토큰 조회 성공",
		content = @Content(schema = @Schema(implementation = WaitingTokenResponse.class)))
	@ApiResponse(responseCode = "404", description = "토큰을 찾을 수 없음")
	@GetMapping
	ResponseEntity<WaitingTokenResponse> getToken(
		@Parameter(description = "header에 WAITING-TOKEN값을 넣어서 요청해주세요")
		@RequestHeader("WAITING-TOKEN") String token
	);
}
