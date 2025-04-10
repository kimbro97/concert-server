package kr.hhplus.be.server.domain.balance;

import static jakarta.persistence.GenerationType.*;
import static lombok.AccessLevel.*;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import kr.hhplus.be.server.domain.BaseEntity;
import kr.hhplus.be.server.domain.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Balance extends BaseEntity {
	@Id
	@Column(name = "balance_id")
	@GeneratedValue(strategy = IDENTITY)
	private Long id;

	@OneToOne
	@JoinColumn(name = "user_id")
	private User user;

	private Long amount;

	public Balance(User user, Long amount) {
		this.user = user;
		this.amount = amount;
	}

	public void charge(Long amount) {
		this.amount = this.amount + amount;
	}

	public void use(Long amount) {
		this.amount = this.amount - amount;
	}
}
