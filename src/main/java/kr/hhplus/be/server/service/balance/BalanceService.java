package kr.hhplus.be.server.service.balance;

import static kr.hhplus.be.server.support.exception.BusinessError.*;

import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.OptimisticLockException;
import kr.hhplus.be.server.domain.balance.Balance;
import kr.hhplus.be.server.domain.balance.BalanceRepository;
import kr.hhplus.be.server.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BalanceService {

	private final BalanceRepository balanceRepository;
	private final UserRepository userRepository;

	@Transactional(readOnly = true)
	public BalanceInfo getBalance(Long userId) {

		userRepository.findById(userId)
			.orElseThrow(NOT_FOUND_USER_ERROR::exception);

		Balance balance = balanceRepository.findByUserId(userId)
			.orElseThrow(NOT_FOUND_BALANCE_ERROR::exception);

		return BalanceInfo.from(balance);

	}

	@Transactional
	public BalanceInfo charge(BalanceCommand.Charge command) {
		try {
			userRepository.findById(command.getUserId())
				.orElseThrow(NOT_FOUND_USER_ERROR::exception);

			Balance balance = balanceRepository.findByUserId(command.getUserId())
				.orElseThrow(NOT_FOUND_BALANCE_ERROR::exception);

			balance.charge(command.getAmount());

			balance = balanceRepository.saveAndFlush(balance);
			return BalanceInfo.from(balance);
		} catch (ObjectOptimisticLockingFailureException | OptimisticLockException e) {
			throw CONCURRENT_POINT_CHARGE_CONFLICT.exception();
		}
	}
}
