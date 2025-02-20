package com.example.blog_board.domain.user.entity;

import com.example.blog_board.common.domain.BaseEntity;
import com.example.blog_board.common.enums.UserRole;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "users")
public class UserEntity extends BaseEntity {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Email(message = "Invalid email format.")
	@Column(unique = true, nullable = false)
	private String email;

	@NotBlank(message = "Password is required")
	@Column(nullable = false)
	private String password;

	@NotNull(message = "Name is required")
	@Column(nullable = false)
	private String name;

	@NotNull(message = "Role is required")
	@Column(nullable = false)
	@Enumerated(jakarta.persistence.EnumType.STRING)
	private UserRole role;

	private String profileImage;
}
