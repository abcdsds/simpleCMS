package book.modules.base;

import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import book.modules.account.Account;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@MappedSuperclass
@EntityListeners(value = {AuditingEntityListener.class})
public abstract class BaseEntity extends BaseTimeEntity {

	
	@CreatedBy
	@JoinColumn(name = "account_id" , updatable = false)
	@ManyToOne(fetch = FetchType.LAZY)
	private Account createdBy;
	
	@LastModifiedBy
	@ManyToOne(fetch = FetchType.LAZY)
	private Account lastModifiedBy;
	
}
