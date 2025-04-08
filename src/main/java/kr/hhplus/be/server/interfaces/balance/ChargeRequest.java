package kr.hhplus.be.server.interfaces.balance;

import kr.hhplus.be.server.domain.balance.BalanceCommand;
import kr.hhplus.be.server.support.exception.BusinessError;

public class ChargeRequest {

	private static final Long MIN_CHARGE_AMOUNT = 0L;

	private final Long userId;
	private final Long amount;

	public ChargeRequest(Long userId, Long amount) {
		this.userId = userId;
		this.amount = amount;
	}

	public BalanceCommand.Charge toCommand() {
		this.validate();
		return new BalanceCommand.Charge(userId, amount);
	}

	private void validate() {
		if (amount <= MIN_CHARGE_AMOUNT) {
			throw BusinessError.CHARGE_AMOUNT_MUST_BE_POSITIVE.exception();
		}
	}
}
