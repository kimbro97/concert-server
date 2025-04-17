package kr.hhplus.be.server.service.balance;

import static org.assertj.core.api.Assertions.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import kr.hhplus.be.server.domain.balance.Balance;
import kr.hhplus.be.server.domain.balance.BalanceRepository;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;

@SpringBootTest
class BalanceServiceConcurrentTest {

	@Autowired private UserRepository userRepository;
	@Autowired private BalanceRepository balanceRepository;

	@Autowired private BalanceService balanceService;

	@Test
	@DisplayName("한 명의 회원이 여러 번 동시에 잔액을 충전해도 최종 금액이 정확해야 한다")
	void concurrent_charge_success() throws InterruptedException {
	    // arrange
		User user = new User("kimbro", "1234");
		userRepository.save(user);

		Balance balance = new Balance(user, 0L);
		balanceRepository.save(balance);

		int threadCount = 10;
		Long chargeAmount = 1000L;
		ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
		CountDownLatch latch = new CountDownLatch(threadCount);

	    // act
		for (int i = 0; i < threadCount; i++) {
			executorService.submit(() -> {
				try {
					BalanceCommand.Charge command = new BalanceCommand.Charge(user.getId(), chargeAmount);
					balanceService.charge(command);
				} finally {
					latch.countDown();
				}
			});
		}

		latch.await();

	    // assert
		Balance finalBalance = balanceRepository.findByUserId(user.getId()).orElseThrow();
		assertThat(finalBalance.getAmount()).isEqualTo(10000L);
	}

}
