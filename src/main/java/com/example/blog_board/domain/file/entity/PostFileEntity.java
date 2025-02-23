package com.example.blog_board.domain.file.entity;

import org.hibernate.annotations.ColumnDefault;

import com.example.blog_board.domain.post.entity.PostEntity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@DiscriminatorValue("POST")
@Table(
	name = "post_files",
	uniqueConstraints = {
		@UniqueConstraint(name = "UNIQUE_POST_FILE", columnNames = { "post_id", "order" })
	}
)
public class PostFileEntity extends FileEntity {

	@Builder
	public PostFileEntity(Long id, String originalFileName, String storedFileName, String filePath, String fileType,
		Long fileSize, PostEntity post, Integer orders) {
		super(id, originalFileName, storedFileName, filePath, fileType, fileSize);
		this.post = post;
		this.orders	= orders;
	}

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "post_id", nullable = false)
	private PostEntity post;

	@Column(nullable = false)
	@ColumnDefault("1")
	private Integer orders;
}
