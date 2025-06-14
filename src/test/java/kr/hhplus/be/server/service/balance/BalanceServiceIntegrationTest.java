package kr.hhplus.be.server.service.balance;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import kr.hhplus.be.server.domain.balance.Balance;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import kr.hhplus.be.server.infras.balance.BalanceJpaRepository;
import kr.hhplus.be.server.support.exception.BusinessException;

@Transactional
@SpringBootTest
class BalanceServiceIntegrationTest {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private BalanceJpaRepository balanceJpaRepository;

	@Autowired
	private BalanceService balanceService;

	@Test
	@DisplayName("존재하지 않은 userId로 조회시 예외가 발생한다.")
	void get_balance_user_exception() {
		// arrange
		Long userId = 1L;

		// act & assert
		assertThatThrownBy(() -> balanceService.getBalance(userId))
			.isInstanceOf(BusinessException.class)
			.hasMessageContaining("유저를 찾을 수 없습니다.");
	}

	@Test
	@DisplayName("유저는 존재하지만 잔액이 존재하지 않으면 예외가 발생한다.")
	void get_balance_exception() {
		// arrange
		User user = new User("kimbro", "1234");
		userRepository.save(user);

		// act & assert
		assertThatThrownBy(() -> balanceService.getBalance(user.getId()))
			.isInstanceOf(BusinessException.class)
			.hasMessageContaining("발란스를 찾을 수 없습니다.");
	}

	@Test
	@DisplayName("존재하는 유저의 Id로 BalanceInfo 조회에 성공한다")
	void get_balance_success() {
		// arrange
		User user = new User("kimbro", "1234");
		userRepository.save(user);
		Balance balance = new Balance(user, 0L);
		balanceJpaRepository.save(balance);

		// act
		BalanceInfo info = balanceService.getBalance(user.getId());

		// assert
		assertThat(info).isNotNull();
		assertThat(info.getBalanceId()).isEqualTo(balance.getId());
		assertThat(info.getAmount()).isEqualTo(0L);
		assertThat(info.getUserId()).isEqualTo(user.getId());
	}

	@Test
	@DisplayName("존재하지 않은 userId로 충전하면 예외가 발생한다")
	void balance_charge_user_exception() {
	    // arrange
		Long userId = 1L;
		Long amount = 1000L;
		BalanceCommand.Charge command = new BalanceCommand.Charge(userId, amount);
		// act & assert
		assertThatThrownBy(() -> balanceService.charge(command))
			.isInstanceOf(BusinessException.class)
			.hasMessageContaining("유저를 찾을 수 없습니다.");
	}

	@Test
	@DisplayName("유저는 존재하지만 잔액이 존재하지 않으면 예외가 발생한다.")
	void balance_charge_balance_exception() {
		// arrange
		Long amount = 1000L;
		User user = new User("kimbro", "1234");
		userRepository.save(user);
		BalanceCommand.Charge command = new BalanceCommand.Charge(user.getId(), amount);
		// act & assert
		assertThatThrownBy(() -> balanceService.charge(command))
			.isInstanceOf(BusinessException.class)
			.hasMessageContaining("발란스를 찾을 수 없습니다.");
	}

	@Test
	@DisplayName("유저가 존재하고 잔액이 존재하면 정상적으로 충전된다.")
	void balance_charge_success() {
	    // arrange
		User user = new User("kimbro", "1234");
		Balance balance = new Balance(user, 0L);
		userRepository.save(user);
		balanceJpaRepository.save(balance);
		BalanceCommand.Charge command = new BalanceCommand.Charge(user.getId(), 10000L);
		// act
		balanceService.charge(command);
		// assert
		Balance updateBalance  = balanceJpaRepository.findByUserId(user.getId()).orElseThrow();
		assertThat(updateBalance.getAmount()).isEqualTo(10000L);
	}

}
