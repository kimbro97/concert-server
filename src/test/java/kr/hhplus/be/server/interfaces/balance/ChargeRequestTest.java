package kr.hhplus.be.server.interfaces.balance;

import static kr.hhplus.be.server.domain.balance.BalanceCommand.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import kr.hhplus.be.server.domain.balance.BalanceCommand;
import kr.hhplus.be.server.support.exception.BusinessException;

class ChargeRequestTest {

	@Test
	@DisplayName("command 객체로 변환할때 amount가 0 또는 음수이면 예오가 발생한다.")
	void amount_test() {

		Long userId = 1L;
		Long zeroAmount = 0L;
		Long minusAmount = -1L;

		ChargeRequest request1 = new ChargeRequest(zeroAmount);
		ChargeRequest request2 = new ChargeRequest(minusAmount);

		assertThatThrownBy(() -> request1.toCommand(userId))
			.isInstanceOf(BusinessException.class);

		assertThatThrownBy(() -> request2.toCommand(userId))
			.isInstanceOf(BusinessException.class);
	}

	@Test
	@DisplayName("toCommand 메서드를 이용해서 Command 객체를 만들 수 있다.")
	void create_command_test() {
		Long userId = 1L;
		Long amount = 1000L;

		ChargeRequest request = new ChargeRequest(amount);

		Charge command = request.toCommand(userId);

		assertThat(command.getAmount()).isEqualTo(amount);

	}

}
