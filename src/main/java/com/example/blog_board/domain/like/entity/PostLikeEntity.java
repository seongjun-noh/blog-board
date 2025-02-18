package com.example.blog_board.domain.like.entity;

import com.example.blog_board.domain.post.entity.PostEntity;

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
@DiscriminatorValue("POST")
public class PostLikeEntity extends LikeEntity {
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "post_id", nullable = false)
	private PostEntity post;
}
