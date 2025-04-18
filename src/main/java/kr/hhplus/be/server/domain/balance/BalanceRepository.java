package kr.hhplus.be.server.domain.balance;

import java.util.Optional;

public interface BalanceRepository {
	Balance save(Balance balance);
	Optional<Balance> findByUserId(Long userId);
}
