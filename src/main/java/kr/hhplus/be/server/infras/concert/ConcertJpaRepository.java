package kr.hhplus.be.server.infras.concert;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.hhplus.be.server.domain.concert.Concert;

public interface ConcertJpaRepository extends JpaRepository<Concert, Long> {

	List<Concert> findAllByIdIn(List<Long> ids);
}
