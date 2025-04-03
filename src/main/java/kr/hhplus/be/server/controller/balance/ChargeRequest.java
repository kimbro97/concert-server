package kr.hhplus.be.server.controller.balance;

public class ChargeRequest {
	private final Long userId;
	private final Long amount;

	public ChargeRequest(Long userId, Long amount) {
		this.userId = userId;
		this.amount = amount;
	}

	public Long getUserId() {
		return userId;
	}

	public Long getAmount() {
		return amount;
	}
}
