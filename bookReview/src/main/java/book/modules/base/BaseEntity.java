package book.modules.base;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import book.modules.account.Account;


@MappedSuperclass
public class BaseEntity {

	@CreatedDate
	@Column(updatable = false)
	private LocalDateTime createdAt;
	
	@LastModifiedDate
	private LocalDateTime updatedAt;
	
	@CreatedBy
	private Account createdBy;
	
	@LastModifiedBy
	private Account lastModifiedBy;
	
}
