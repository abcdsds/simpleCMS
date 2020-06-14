package book.modules.admin;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import book.modules.WithAccount;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Rollback(value = true)
@Transactional
@AutoConfigureMockMvc
@SpringBootTest
class AdminControllerTest {

	@Autowired private MockMvc mockMvc;
	
	@WithAccount("test01")
	@DisplayName("일반회원 어드민페이지 접속 불가 ")
	@Test
	void adminPageWithRoleUser() throws Exception {
		
		mockMvc.perform(get("/admin"))
				.andExpect(status().is4xxClientError());
		
	}
	
	@WithAccount("admintest01")
	@DisplayName("관리자 어드민페이지 접속 가능")
	@Test
	void adminPageWithRoleAdmin() throws Exception {
		
		mockMvc.perform(get("/admin"))
				.andExpect(status().isOk())
				.andExpect(model().attributeExists("accountStatistics"))
				.andExpect(model().attributeExists("postStatistics"))
				.andExpect(model().attributeExists("commentStatistics"))
				.andExpect(view().name("admin/index"));
		
	}
}
