package kr.hhplus.be.server.domain.balance;

import java.util.Optional;

public interface BalanceRepository {
	BalanceEntity save(BalanceEntity balanceEntity);
	Optional<BalanceEntity> findByUserId(Long userId);
}
