package kr.hhplus.be.server.interfaces.balance;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import kr.hhplus.be.server.interfaces.api.balance.ChargeRequest;
import kr.hhplus.be.server.service.balance.BalanceCommand;
import kr.hhplus.be.server.support.exception.BusinessException;

class ChargeRequestTest {
	@Test
	@DisplayName("유효한 금액으로 toCommand 호출 시 BalanceCommand.Charge가 반환된다")
	void toCommand_success() {
		Long userId = 1L;
		Long amount = 1000L;
		ChargeRequest request = new ChargeRequest(amount);

		BalanceCommand.Charge command = request.toCommand(userId);

		assertThat(command.getUserId()).isEqualTo(userId);
		assertThat(command.getAmount()).isEqualTo(amount);
	}

	@Test
	@DisplayName("금액이 0 이하이면 예외가 발생한다")
	void toCommand_exception() {

		ChargeRequest request = new ChargeRequest(0L);

		assertThatThrownBy(() -> request.toCommand(1L))
			.isInstanceOf(BusinessException.class);
	}
}
