package kr.hhplus.be.server.domain.balance;

import lombok.Builder;
import lombok.Getter;

@Getter
public class BalanceInfo {
	private final Long balanceId;
	private final Long userId;
	private final Long amount;

	@Builder
	private BalanceInfo(Long balanceId, Long userId, Long amount) {
		this.balanceId = balanceId;
		this.userId = userId;
		this.amount = amount;
	}

	public static BalanceInfo from(BalanceEntity balance) {
		return BalanceInfo.builder()
			.balanceId(balance.getId())
			.userId(balance.getUser().getId())
			.amount(balance.getAmount())
			.build();
	}
}
