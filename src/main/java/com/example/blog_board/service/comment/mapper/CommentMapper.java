package com.example.blog_board.service.comment.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import com.example.blog_board.api.comment.dto.response.CommentResponse;
import com.example.blog_board.domain.comment.entity.CommentEntity;

@Mapper(
	componentModel = "spring",
	unmappedTargetPolicy = ReportingPolicy.ERROR,
	nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface CommentMapper {

	@Mapping(target = "userId", source = "user.id")
	@Mapping(target = "userName", source = "user.name")
	@Mapping(target = "postId", source = "post.id")
	@Mapping(target = "parentCommentId", source = "parentComment.id")
	CommentResponse toDto(CommentEntity comment);
}