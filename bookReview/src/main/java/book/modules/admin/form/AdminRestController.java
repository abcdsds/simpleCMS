package book.modules.admin.form;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import book.modules.account.form.AccountListForm;
import book.modules.admin.AdminService;
import book.modules.board.BoardService;
import book.modules.board.form.BoardFormValidator;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/admin")
@RestController
public class AdminRestController {

	private final AdminService adminService;
	
	@GetMapping("/accountList")
	public Page<AccountListForm> accountPaging(@PageableDefault(size = 12, page = 0 , sort = "id" , direction = Sort.Direction.DESC) 
													Pageable pageable) {
		return adminService.getAccounts(pageable);
	}
}
