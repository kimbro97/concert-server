package kr.hhplus.be.server.domain.concert;

import static jakarta.persistence.GenerationType.*;
import static lombok.AccessLevel.*;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import kr.hhplus.be.server.domain.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Concert extends BaseEntity {

	@Id
	@Column(name = "concert_id")
	@GeneratedValue(strategy = IDENTITY)
	private Long id;

	private String title;

	public Concert(String title) {
		this.title = title;
	}
}
