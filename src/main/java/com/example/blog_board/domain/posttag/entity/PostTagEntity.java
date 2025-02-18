package com.example.blog_board.domain.posttag.entity;

import com.example.blog_board.common.domain.BaseEntity;
import com.example.blog_board.domain.post.entity.PostEntity;
import com.example.blog_board.domain.tag.entity.TagEntity;
import com.example.blog_board.domain.user.entity.UserEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Table(name = "post_tags")
public class PostTagEntity extends BaseEntity {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "post_id", nullable = false)
	private PostEntity post;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tag_id", nullable = false)
	private TagEntity tag;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private UserEntity user;
}
