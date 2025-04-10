package kr.hhplus.be.server.service.balance;

import lombok.Getter;

public class BalanceCommand {

	@Getter
	public static class Charge {

		private final Long userId;
		private final Long amount;

		public Charge(Long userId, Long amount) {
			this.userId = userId;
			this.amount = amount;
		}
	}

}
