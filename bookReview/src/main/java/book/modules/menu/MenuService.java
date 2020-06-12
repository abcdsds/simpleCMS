package book.modules.menu;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import book.modules.account.Account;
import book.modules.board.Board;
import book.modules.board.BoardRepository;
import book.modules.menu.form.MenuForm;
import book.modules.menu.form.MenuMessageForm;
import book.modules.simple.SimpleMessageType;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;

@Transactional
@RequiredArgsConstructor
@Service
public class MenuService {

	private final MenuRepository menuRepository;
	private final BoardRepository boardRepository;
	private final ModelMapper modelMapper;
	private final ObjectMapper objectMapper;
	
	public void addMenu(MenuForm menuForm) throws NotFoundException {
		
		Menu menu = Menu.builder().name(menuForm.getName()).type(menuForm.getType()).build();
		
		if (menuForm.getParentId() != null) {
			Optional<Menu> findById = menuRepository.findById(menuForm.getParentId());	
			Menu getParent = findById.orElseThrow(() -> new NotFoundException("상위 메뉴가 존재하지 않습니다."));
			menu.addParentAndSub(getParent);
		}

		if (menuForm.getBoardId() != null) {
			Optional<Board> findById = boardRepository.findById(menuForm.getBoardId());
			Board getBoard = findById.orElseThrow(() -> new NotFoundException("게시판이 존재하지 않습니다."));
			menu.addBoardAndRoleAndPath(getBoard, getBoard.getRole() , getBoard.getPath());
		} else {
			
			menu.addPathAndRole(menuForm.getPath(), new SimpleGrantedAuthority(menuForm.getRole()));
			
		}
		
		menuRepository.save(menu);
		
	}

	public void updateMenu(Long id, MenuForm menuForm) throws NotFoundException {
		// TODO Auto-generated method stub
		Optional<Menu> getMenu = menuRepository.findById(id);
		Menu menu = getMenu.orElseThrow(() -> new NotFoundException("상위 메뉴가 존재하지 않습니다."));
		modelMapper.map(menuForm, menu);
		
		if (menuForm.getParentId() != null) {
			Optional<Menu> findById = menuRepository.findById(menuForm.getParentId());	
			Menu getParent = findById.orElseThrow(() -> new NotFoundException("상위 메뉴가 존재하지 않습니다."));
			menu.addParentAndSub(getParent);
		}
		
		if (menuForm.getBoardId() != null) {
			Optional<Board> findById = boardRepository.findById(menuForm.getBoardId());
			Board getBoard = findById.orElseThrow(() -> new NotFoundException("게시판이 존재하지 않습니다."));
			menu.addBoardAndRoleAndPath(getBoard, getBoard.getRole() , getBoard.getPath());
		} else {
			
			menu.addPathAndRole(menuForm.getPath(), new SimpleGrantedAuthority(menuForm.getRole()));
			
		}
	}

	public String deleteMenu(Long menuId) throws JsonProcessingException {
		// TODO Auto-generated method stub
		MenuMessageForm form = new MenuMessageForm();
		Menu getMenu = menuRepository.findMenuAndSubMenuById(menuId);
		
		if (getMenu == null) {
			form.setMessage("메뉴가 존재하지 않습니다.");
			form.setType(SimpleMessageType.FAIL);
			
			return objectMapper.writeValueAsString(form);
		}
		
		
		if (!getMenu.getSubMenus().isEmpty()) {
			
			form.setMessage("하위메뉴가 있는 메뉴는 삭제할수 없습니다.");
			form.setType(SimpleMessageType.FAIL);
			
			return objectMapper.writeValueAsString(form);
		}

		Menu parent = getMenu.getParent();
		if (parent != null) {
			parent.getSubMenus().remove(getMenu);
		}
		
		menuRepository.delete(getMenu);
		
		form.setMessage("메뉴가 성공적으로 삭제되었습니다.");
		form.setType(SimpleMessageType.SUCCESS);
		
		return objectMapper.writeValueAsString(form);
	}
	
	public List<Menu> getMenuWithSubmenu(Account account) {
		
		if (account == null) {
			return menuRepository.findAllByRole(new SimpleGrantedAuthority("ROLE_USER"));
		}
		
		return menuRepository.findAllByRole(new SimpleGrantedAuthority(account.getRole()));
	}
}
