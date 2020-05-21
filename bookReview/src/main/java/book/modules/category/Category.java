package book.modules.category;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(of = "id")
@Entity
public class Category {
	
	@Id @GeneratedValue
	private Long id;
	
	private String name;
	

}
