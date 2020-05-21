package book.modules.base;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;


@MappedSuperclass
public class BaseTimeEntity {

	@CreatedDate
	@Column(updatable = false)
	private LocalDateTime createdAt;
	
	@LastModifiedDate
	private LocalDateTime updatedAt; 
}
