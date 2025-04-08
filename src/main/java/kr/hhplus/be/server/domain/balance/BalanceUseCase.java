package kr.hhplus.be.server.domain.balance;

import static kr.hhplus.be.server.support.exception.BusinessError.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BalanceUseCase {

	private final BalanceRepository balanceRepository;

	@Transactional(readOnly = true)
	public BalanceInfo getBalance(Long userId) {

		BalanceEntity balance = balanceRepository.findByUserId(userId)
			.orElseThrow(NOT_FOUND_BALANCE_ERROR::exception);

		return BalanceInfo.from(balance);

	}

	@Transactional
	public BalanceInfo charge(BalanceCommand.Charge command) {

		BalanceEntity balance = balanceRepository.findByUserId(command.getUserId())
			.orElseThrow(NOT_FOUND_BALANCE_ERROR::exception);

		balance.charge(command.getAmount());

		balance = balanceRepository.save(balance);
		return BalanceInfo.from(balance);

	}
}
