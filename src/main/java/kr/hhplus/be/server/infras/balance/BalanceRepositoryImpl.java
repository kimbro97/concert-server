package kr.hhplus.be.server.infras.balance;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import kr.hhplus.be.server.domain.balance.BalanceEntity;
import kr.hhplus.be.server.domain.balance.BalanceRepository;

@Repository
public class BalanceRepositoryImpl implements BalanceRepository {
	@Override
	public BalanceEntity save(BalanceEntity balanceEntity) {
		return null;
	}

	@Override
	public Optional<BalanceEntity> findByUserId(Long userId) {
		return Optional.empty();
	}
}
