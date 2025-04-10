package kr.hhplus.be.server.domain.balance;

import java.util.Optional;

public interface BalanceRepository {
	Balance save(Balance balanceEntity);
	Optional<Balance> findByUserId(Long userId);
}
