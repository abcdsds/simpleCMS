package book.modules.board.form;


import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import lombok.Data;

@Data
public class BoardForm {

	private Long id;
	
	@NotBlank
	private String name;
	
	@Pattern(regexp = "^[ㄱ-ㅎ가-힣a-zA-Z0-9_-]{2,20}$")
	@NotBlank
	private String path;
	
	@NotBlank
	private String role;
}
