package com.example.blog_board.domain.like.entity;

import com.example.blog_board.common.domain.BaseEntity;
import com.example.blog_board.domain.user.entity.UserEntity;

import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "target_type", discriminatorType = DiscriminatorType.STRING)
@Table(
	name = "likes",
	uniqueConstraints = {
		@UniqueConstraint(columnNames = {"user_id", "post_id"}),
		@UniqueConstraint(columnNames = {"user_id", "comment_id"})
	}
)
public abstract class LikeEntity extends BaseEntity {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	private UserEntity user;
}

