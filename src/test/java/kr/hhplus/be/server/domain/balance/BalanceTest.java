package kr.hhplus.be.server.domain.balance;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.support.exception.BusinessException;

class BalanceTest {

	@Test
	@DisplayName("충전금액을 받아서 보유금액을 증가시킬 수 있다.")
	void charge_test() {
		User user = new User("kimbro", "1234");
		Balance balance = new Balance(user, 1000L);

		balance.charge(10000L);

		assertThat(balance.getAmount()).isEqualTo(11000L);
	}

	@Test
	@DisplayName("Balance 생성 시 유저와 초기 금액이 설정된다")
	void constructor_setsUserAndAmount() {
		User user = new User("kim", "pw");
		Balance balance = new Balance(user, 5000L);

		assertThat(balance.getUser()).isEqualTo(user);
		assertThat(balance.getAmount()).isEqualTo(5000L);
	}

	@Test
	@DisplayName("사용금액이 보유금액을 초과하면 예외가 발생한다.")
	void balance_use_exception() {
		User user = new User("kimbro", "1234");
		Balance balance = new Balance(user, 5000L);

		assertThatThrownBy(() -> balance.use(5001L))
			.isInstanceOf(BusinessException.class);

	}

	@Test
	@DisplayName("사용금액이 보유금액을 초과하지않으면 정상적으로 사용된다.")
	void balance_use() {
		User user = new User("kimbro", "1234");
		Balance balance = new Balance(user, 5000L);

		balance.use(5000L);

		assertThat(balance.getAmount()).isEqualTo(0L);
	}

}
