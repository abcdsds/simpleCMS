package book.modules.board;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(of = "id")
@Entity
public class Board {
	
	@Id @GeneratedValue
	private Long id;
	
	private String name;
	

}
