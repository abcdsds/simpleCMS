package book.modules.board.form;


import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import book.modules.board.Board;
import book.modules.board.BoardRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class BoardFormValidator implements Validator{
	
	private final BoardRepository boardRepository;
	
	@Override
	public boolean supports(Class<?> clazz) {
		// TODO Auto-generated method stub
		return clazz.isAssignableFrom(BoardForm.class);
	}

	@Override
	public void validate(Object target, Errors errors) {
		// TODO Auto-generated method stub
		BoardForm form = (BoardForm)target;
		
		Board findByPath = boardRepository.findByPath(form.getPath());
		
		if (findByPath != null) {
			if (!findByPath.getId().equals(form.getId())) {
				errors.rejectValue("path", "path-reduplication " , "경로가 중복됩니다. 다시 설정해주세요.");
			} 
		}
		
	}

}
