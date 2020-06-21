package book.modules.menu.form;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import book.modules.menu.MenuType;

@Component
public class MenuFormValidator implements Validator{
	
	@Override
	public boolean supports(Class<?> clazz) {
		// TODO Auto-generated method stub
		return clazz.isAssignableFrom(MenuForm.class);
	}

	@Override
	public void validate(Object target, Errors errors) {
		// TODO Auto-generated method stub
		MenuForm form = (MenuForm)target;
		
		
		if (!form.getType().equals(MenuType.BOARD)) {
			
			System.out.println("===========================");
			System.out.println(form.getPath());
			
			if (form.getPath() == null || form.getPath().equals("")) {
				errors.rejectValue("path", "empty " , "경로를 입력해주세요.");
			}
		} else {
			
			
			if (form.getBoardId() == null) {
				errors.rejectValue("boardId", "empty " , "게시판을 정해주세요.");
			}
		}
		
		
		
		
	}

}
