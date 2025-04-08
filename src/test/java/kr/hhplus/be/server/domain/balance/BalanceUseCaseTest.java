package kr.hhplus.be.server.domain.balance;

import static kr.hhplus.be.server.domain.balance.BalanceCommand.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import kr.hhplus.be.server.domain.user.UserEntity;
import kr.hhplus.be.server.support.exception.BusinessException;

@ExtendWith(MockitoExtension.class)
class BalanceUseCaseTest {

	@Mock
	private BalanceRepository balanceRepository;

	@InjectMocks
	private BalanceUseCase balanceUseCase;

	@Test
	void getBalance_성공() {
		Long userId = 1L;
		UserEntity user = new UserEntity("kimbro", "1234");
		BalanceEntity balance = new BalanceEntity(user, 1000L);

		when(balanceRepository.findByUserId(userId)).thenReturn(Optional.of(balance));

		BalanceInfo info = balanceUseCase.getBalance(userId);
		assertThat(info.getAmount()).isEqualTo(1000L);
	}

	@Test
	void getBalance_실패() {
		Long userId = 1L;

		when(balanceRepository.findByUserId(userId)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> balanceUseCase.getBalance(userId))
			.isInstanceOf(BusinessException.class);
	}

	@Test
	void charge_성공() {
		Long userId = 1L;
		UserEntity user = new UserEntity("kimbro", "1234");
		BalanceEntity balance = new BalanceEntity(user, 3000L);
		Charge command = new Charge(userId, 1000L);
		BalanceEntity chargeBalance = new BalanceEntity(user, 4000L);

		when(balanceRepository.findByUserId(userId)).thenReturn(Optional.of(balance));
		when(balanceRepository.save(balance)).thenReturn(chargeBalance);

		BalanceInfo info = balanceUseCase.charge(command);

		assertThat(info.getAmount()).isEqualTo(4000L);
	}

	@Test
	void charge_실패() {
		Long userId = 1L;
		Charge command = new Charge(userId, 1000L);

		when(balanceRepository.findByUserId(userId)).thenReturn(Optional.empty());
		assertThatThrownBy(() -> balanceUseCase.charge(command))
			.isInstanceOf(BusinessException.class);

		verify(balanceRepository, times(0)).save(new BalanceEntity());

	}

}
