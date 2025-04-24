package kr.hhplus.be.server.infras.balance;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import kr.hhplus.be.server.domain.balance.Balance;

public interface BalanceJpaRepository extends JpaRepository<Balance, Long> {
	Optional<Balance> findByUserId(Long userId);

	@Lock(LockModeType.OPTIMISTIC)
	@Query("SELECT b FROM Balance b WHERE b.user.id= :userId")
	Optional<Balance> findByUserIdWithLock(@Param("userId") Long userId);
}
