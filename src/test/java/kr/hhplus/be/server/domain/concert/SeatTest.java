package kr.hhplus.be.server.domain.concert;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import kr.hhplus.be.server.support.exception.BusinessError;
import kr.hhplus.be.server.support.exception.BusinessException;

class SeatTest {
	@Test
	@DisplayName("좌석의 가격을 조회할 수 있다")
	void calculatePrice_정상() {
		// arrange
		Seat seat = new Seat(null, "A1", 12000L, true);

		// act
		Long price = seat.calculatePrice();

		// assert
		assertThat(price).isEqualTo(12000L);
	}

	@Test
	@DisplayName("예약 가능한 좌석이면 reserve 호출 시 상태가 false로 변경된다")
	void reserve_성공() {
		// arrange
		Seat seat = new Seat(null, "A2", 15000L, true);

		// act
		seat.reserve();

		// assert
		assertThat(seat.getIsSelectable()).isFalse();
	}

	@Test
	@DisplayName("예약 불가능한 좌석이면 reserve 호출 시 예외가 발생하고 상태는 변경되지 않는다")
	void reserve_예외() {
		// arrange
		Seat seat = new Seat(null, "A3", 10000L, false);

		// act & assert
		assertThatThrownBy(seat::reserve)
			.isInstanceOf(BusinessException.class)
			.hasMessageContaining(BusinessError.ALREADY_RESERVED_SEAT.getMessage());

		// assert: 상태가 여전히 false인지 확인 (변경 안 됨)
		assertThat(seat.getIsSelectable()).isFalse();
	}
}
