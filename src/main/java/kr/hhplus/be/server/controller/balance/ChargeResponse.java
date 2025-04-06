package kr.hhplus.be.server.controller.balance;

public class ChargeResponse {
	private final Long amount;

	public ChargeResponse(Long amount) {
		this.amount = amount;
	}

	public Long getAmount() {
		return amount;
	}
}
