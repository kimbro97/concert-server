package kr.hhplus.be.server.service.balance;

import static org.assertj.core.api.Assertions.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import kr.hhplus.be.server.domain.balance.Balance;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.infras.balance.BalanceJpaRepository;
import kr.hhplus.be.server.infras.user.UserJpaRepository;

@SpringBootTest
class BalanceServiceConcurrentTest {

	@Autowired
	private UserJpaRepository userJpaRepository;

	@Autowired
	private BalanceJpaRepository balanceJpaRepository;

	@Autowired
	private BalanceService balanceService;

	@Test
	@DisplayName("한 명의 회원이 2번 동시에 잔액을 충전하면 1번 성공, 1번 실패하여 최종 금액은 1000원이다.")
	void concurrent_charge_success() throws InterruptedException {
	    // arrange
		User user = new User("kimbro", "1234");
		userJpaRepository.save(user);

		Balance balance = new Balance(user, 0L);
		balanceJpaRepository.save(balance);

		int threadCount = 2;
		Long chargeAmount = 1000L;
		ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
		CountDownLatch latch = new CountDownLatch(threadCount);

		AtomicInteger successCount = new AtomicInteger(0);
		AtomicInteger failCount = new AtomicInteger(0);

		// act
		for (int i = 0; i < threadCount; i++) {
			executorService.submit(() -> {
				try {
					BalanceCommand.Charge command = new BalanceCommand.Charge(user.getId(), chargeAmount);
					balanceService.charge(command);
					successCount.incrementAndGet();
				} catch (Exception e) {
					failCount.incrementAndGet();
				} finally {
					latch.countDown();
				}
			});
		}

		latch.await();

	    // assert
		Balance finalBalance = balanceJpaRepository.findByUserId(user.getId()).orElseThrow();
		assertThat(finalBalance.getAmount()).isEqualTo(1000L);
		assertThat(successCount.get()).isEqualTo(1);
		assertThat(failCount.get()).isEqualTo(1);
	}

}
