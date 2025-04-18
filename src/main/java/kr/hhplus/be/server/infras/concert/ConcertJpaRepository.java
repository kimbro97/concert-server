package kr.hhplus.be.server.infras.concert;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.hhplus.be.server.domain.concert.Concert;

public interface ConcertJpaRepository extends JpaRepository<Concert, Long> {
}
