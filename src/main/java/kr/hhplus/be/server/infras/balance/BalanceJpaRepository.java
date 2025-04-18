package kr.hhplus.be.server.infras.balance;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.hhplus.be.server.domain.balance.Balance;

public interface BalanceJpaRepository extends JpaRepository<Balance, Long> {
	Optional<Balance> findByUserId(Long userId);
}
