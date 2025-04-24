package kr.hhplus.be.server.service.balance;

import static kr.hhplus.be.server.service.balance.BalanceCommand.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import kr.hhplus.be.server.domain.balance.Balance;
import kr.hhplus.be.server.domain.balance.BalanceRepository;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import kr.hhplus.be.server.support.exception.BusinessException;

@ExtendWith(MockitoExtension.class)
class BalanceServiceUnitTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private BalanceRepository balanceRepository;

	@InjectMocks
	private BalanceService balanceService;

	@Test
	@DisplayName("유저 정보가 존재하지 않으면 예외를 발생시키고, 잔액 조회는 수행하지 않는다")
	void getBalance_user_find_exception() {
		Long userId = 1L;

		when(userRepository.findById(userId)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> balanceService.getBalance(userId))
			.isInstanceOf(BusinessException.class);

		verify(balanceRepository, times(0)).findByUserId(userId);
	}

	@Test
	@DisplayName("잔액조회시 존재하지 않으면 예외가 발생한다.")
	void getBalance_balance_find_exception() {
		Long userId = 1L;
		User user = new User("kimbro", "1234");

		when(userRepository.findById(userId)).thenReturn(Optional.of(user));

		when(balanceRepository.findByUserId(userId)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> balanceService.getBalance(userId))
			.isInstanceOf(BusinessException.class);
	}


	@Test
	@DisplayName("예외가 발생하지 않으면 잔액조회에 성공한다.")
	void getBalance_success() {
		Long userId = 1L;
		User user = new User("kimbro", "1234");
		Balance balance = new Balance(user, 1000L);

		when(userRepository.findById(userId)).thenReturn(Optional.of(user));

		when(balanceRepository.findByUserId(userId)).thenReturn(Optional.of(balance));

		BalanceInfo info = balanceService.getBalance(userId);
		assertThat(info.getAmount()).isEqualTo(1000L);
	}

	@Test
	@DisplayName("유저 정보가 존재하지 않으면 예외를 발생시키고, 이후 로직은 수행하지 않는다.")
	void charge_user_find_exception() {
		Long userId = 1L;
		Charge command = new Charge(userId, 1000L);

		when(userRepository.findById(userId)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> balanceService.charge(command))
			.isInstanceOf(BusinessException.class);

		verify(balanceRepository, times(0)).findByUserId(userId);
		verify(balanceRepository, times(0)).save(any(Balance.class));
	}

	@Test
	@DisplayName("유저 정보가 존재하고 잔액 정보가 존재하지 않으면 예외가 발생하고 이후 로직은 실행되지 않는다.")
	void charge_balance_find_exception() {
		Long userId = 1L;
		User user = new User("kimbro", "1234");
		Charge command = new Charge(userId, 1000L);

		when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		when(balanceRepository.findByUserId(userId)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> balanceService.charge(command))
			.isInstanceOf(BusinessException.class);

		verify(balanceRepository, times(0)).save(any(Balance.class));
	}

	@Test
	@DisplayName("예외가 발생하지 않으면 잔액 충전에 성공한다.")
	void charge_성공() {
		Long userId = 1L;
		User user = new User("kimbro", "1234");
		Balance balance = new Balance(user, 3000L);
		Charge command = new Charge(userId, 1000L);
		Balance chargeBalance = new Balance(user, 4000L);

		when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		when(balanceRepository.findByUserId(userId)).thenReturn(Optional.of(balance));
		when(balanceRepository.saveAndFlush(balance)).thenReturn(chargeBalance);

		BalanceInfo info = balanceService.charge(command);

		assertThat(info.getAmount()).isEqualTo(4000L);
	}
}
