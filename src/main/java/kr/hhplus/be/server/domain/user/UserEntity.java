package kr.hhplus.be.server.domain.user;

import static jakarta.persistence.GenerationType.*;
import static lombok.AccessLevel.*;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import kr.hhplus.be.server.domain.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "users")
@NoArgsConstructor(access = PROTECTED)
public class UserEntity extends BaseEntity {
	@Id
	@Column(name = "user_id")
	@GeneratedValue(strategy = IDENTITY)
	private Long id;

	private String username;
	private String password;

	public UserEntity(String username, String password) {
		this.username = username;
		this.password = password;
	}
}
