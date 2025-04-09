package kr.hhplus.be.server.interfaces.balance;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import kr.hhplus.be.server.domain.balance.BalanceInfo;
import kr.hhplus.be.server.domain.balance.BalanceUseCase;
import kr.hhplus.be.server.interfaces.common.ApiResponse;
import kr.hhplus.be.server.support.resolver.UserId;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class BalanceController implements BalanceControllerDocs {

	private final BalanceUseCase balanceUseCase;

	@PutMapping("/api/v1/balance/charge")
	public ApiResponse<BalanceInfo> charge(@UserId Long userId, @RequestBody ChargeRequest request) {
		return ApiResponse.OK(balanceUseCase.charge(request.toCommand(userId)));
	}

	@GetMapping("/api/v1/balance")
	public ApiResponse<BalanceInfo> getBalance(@UserId Long userId) {
		return ApiResponse.OK(balanceUseCase.getBalance(userId));
	}
}
