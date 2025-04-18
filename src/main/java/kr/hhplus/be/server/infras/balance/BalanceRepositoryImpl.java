package kr.hhplus.be.server.infras.balance;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import kr.hhplus.be.server.domain.balance.Balance;
import kr.hhplus.be.server.domain.balance.BalanceRepository;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class BalanceRepositoryImpl implements BalanceRepository {

	private final BalanceJpaRepository balanceJpaRepository;

	@Override
	public Balance save(Balance balance) {
		return balanceJpaRepository.save(balance);
	}

	@Override
	public Optional<Balance> findByUserId(Long userId) {
		return balanceJpaRepository.findByUserId(userId);
	}
}
