package book.modules.board.manager;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import book.modules.account.Account;
import book.modules.board.Board;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(of = "id")
@Builder @Getter
@Entity @AllArgsConstructor @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BoardManager {

	@Column(name = "bmanager_id")
	@Id @GeneratedValue
	private Long id;
	
	@JoinColumn(name = "board_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private Board board;
	
	@JoinColumn(name = "account_id" , updatable = false)
	@ManyToOne(fetch = FetchType.LAZY)
	private Account managedBy;
	
	@Column(updatable = false)
	private LocalDateTime managedAt;
	
	public void deleteManager() {
		// TODO Auto-generated method stub
		this.managedBy.getManagers().remove(this);
		this.board = null;
	}
	
	public void deleteBoard() {
		this.board = null;
	}
	
	
}
