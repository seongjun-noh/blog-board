package com.example.blog_board.domain.post.entity;

import java.util.List;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.SQLDelete;

import com.example.blog_board.common.domain.BaseEntity;
import com.example.blog_board.domain.file.entity.PostFileEntity;
import com.example.blog_board.domain.user.entity.UserEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "posts")
@SQLDelete(sql = "UPDATE posts SET is_deleted = true WHERE id = ?")
public class PostEntity extends BaseEntity {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 100)
	private String title;

	@Column(columnDefinition = "TEXT", nullable = false)
	private String content;

	@Column(nullable = false)
	@ColumnDefault("0")
	@Builder.Default
	private Integer viewCount = 0;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private UserEntity user;

	@OneToMany(mappedBy = "post", fetch = FetchType.LAZY)
	private List<PostFileEntity> files;

	@Column(nullable = false)
	@ColumnDefault("0")
	@Builder.Default
	private Boolean isDeleted = false;


	// 글 내용을 maxLength 길이만큼 자르고 ...을 붙여 반환
	public String getTruncateContent(int maxLength) {

		return this.content.length() <= maxLength ?
				this.content :
				this.content.substring(0, maxLength) + "...";
	}

	public boolean hasFiles() {
		return this.files.size() > 0;
	}

	public void addViewCount() {
		this.viewCount += 1;
	}

	public void updateTitle(String title) {
		this.title = title;
	}

	public void updateContent(String content) {
		this.content = content;
	}
}
