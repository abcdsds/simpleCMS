package book.modules.menu.form;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import book.modules.menu.MenuType;
import lombok.Data;

@Data
public class MenuForm {

	private Long id;
	
	@NotBlank
	private String name;
	
	private String path;
	
	private String role;
	
	@NotNull
	private MenuType type;
	
	private Long boardId;
	
	private Long parentId;
	
	private String boardName;
}
