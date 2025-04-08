package kr.hhplus.be.server.interfaces.balance;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.domain.balance.BalanceInfo;

@Tag(name = "Balance", description = "유저 잔액 관련 API")
public interface BalanceControllerDocs {

	@Operation(summary = "잔액 충전", description = "요청한 금액만큼 유저의 잔액을 충전합니다.")
	kr.hhplus.be.server.interfaces.common.ApiResponse<BalanceInfo> charge(
		@Parameter(name = "X-USER-ID", description = "유저 ID (헤더)", required = true, in = ParameterIn.HEADER)
		Long userId,

		@RequestBody
		@Parameter(description = "충전 요청", required = true)
		ChargeRequest request
	);

	@Operation(summary = "잔액 조회", description = "유저의 현재 잔액을 조회합니다.")
	kr.hhplus.be.server.interfaces.common.ApiResponse<BalanceInfo> getBalance(
		@Parameter(description = "로그인한 유저 ID", required = true)
		Long userId
	);
}
