package kr.hhplus.be.server.infras.token;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import kr.hhplus.be.server.domain.token.Token;
import kr.hhplus.be.server.domain.token.TokenStatus;

public interface TokenJpaRepository extends JpaRepository<Token, Long> {
	Optional<Token> findByUuid(String uuid);

	@Query("""
    SELECT COUNT(t)
    FROM Token t
    WHERE t.schedule.id = :scheduleId
      AND t.status = :status
      AND t.createdAt < (
        SELECT t2.createdAt
        FROM Token t2
        WHERE t2.uuid = :uuid
      )
""")
	Long findTokenLocation(@Param("scheduleId") Long scheduleId,
		@Param("uuid") String uuid,
		@Param("status") TokenStatus status);

	List<Token> findAllByScheduleIdAndStatusOrderByCreatedAtAsc(Long scheduleId, TokenStatus status);
	Long countByScheduleIdAndStatus(Long scheduleId, TokenStatus status);
	void deleteByUuid(String uuid);
	List<Token> findAllByScheduleIdAndStatusAndExpireAtBefore(Long scheduleId, TokenStatus status, LocalDateTime expireAt);

	List<Token> findByStatus(TokenStatus status);
}
