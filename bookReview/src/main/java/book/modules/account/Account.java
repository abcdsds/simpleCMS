package book.modules.account;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import book.modules.base.BaseTimeEntity;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode(of = "id" , callSuper = true)
@Getter @Builder
@Entity
public class Account extends BaseTimeEntity{

	@Id @GeneratedValue
	private Long id;
}
