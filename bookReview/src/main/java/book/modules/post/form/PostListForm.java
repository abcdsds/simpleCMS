package book.modules.post.form;

import java.time.LocalDateTime;

import com.querydsl.core.annotations.QueryProjection;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PostListForm {

	private Long id;
	
	private String title;
	
	private LocalDateTime createdAt;
	
	private String createdBy;
	
	private int up;
	
	private int views;

	@QueryProjection
	public PostListForm(Long id, String title, LocalDateTime createdAt, String createdBy, int up, int views) {
		this.id = id;
		this.title = title;
		this.createdAt = createdAt;
		this.createdBy = createdBy;
		this.up = up;
		this.views = views;
	}
	
	
}
