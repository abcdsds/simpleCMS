package book.modules.comment;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedSubgraph;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

import book.modules.account.UserAccount;
import book.modules.base.BaseEntity;
import book.modules.post.Post;
import book.modules.post.vote.PostVote;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NamedEntityGraph(
        name = "Comment.withParentAndGroupAndChildList",
        attributeNodes = {
        		@NamedAttributeNode("createdBy"),
        		@NamedAttributeNode("parent"),
                @NamedAttributeNode(value = "group", subgraph = "childList")
        },
        subgraphs = @NamedSubgraph(name = "childList", attributeNodes = @NamedAttributeNode("childList"))
)

@EqualsAndHashCode(of = "id" , callSuper = true)
@Entity @AllArgsConstructor @NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter @Builder
public class Comment extends BaseEntity {

	@Id @GeneratedValue
	@Column(name = "comment_id")
	private Long id;
	
	private String content;
	
	private int up;
	
	private int down;
	
	private boolean best;
	
	private boolean deleted;
	
	private int depth;
	
	@JoinColumn(name = "post_id")
	@ManyToOne
	private Post post;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_id")
	private Comment parent;
	
	@OneToMany(mappedBy = "post")
	private List<PostVote> voteList = new ArrayList<PostVote>();
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "group_id")
	private Comment group;
	
	@OrderBy("id")
	@OneToMany(mappedBy = "group")
	private Set<Comment> childList = new LinkedHashSet<Comment>();
	
	public void updateParent (Comment parent) {
		this.parent = parent;
	}
	
	public void updateChild (Comment child) {
		this.childList.add(child);
	}
	
	public void updateGroup(Comment group) {
		this.group = group;
	}

	public void voteUp() {
		// TODO Auto-generated method stub
		this.up++;
	}

	public void voteDown() {
		// TODO Auto-generated method stub
		this.down++;
	}
	
	public boolean isCreatedBy(UserAccount userAccount) {
		
		return super.getCreatedBy().getId() == userAccount.getAccount().getId();
	}

	public void updateContent(String content) {
		// TODO Auto-generated method stub
		this.content = content;
	}

	public void updateDeleted(boolean deleted) {
		// TODO Auto-generated method stub
		this.deleted = deleted;
	}
}
