package com.example.blog_board.domain.like.entity;

import com.example.blog_board.domain.comment.entity.CommentEntity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@DiscriminatorValue("COMMENT")
public class CommentLikeEntity extends LikeEntity {
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "comment_id", nullable = false)
	private CommentEntity comment;
}
