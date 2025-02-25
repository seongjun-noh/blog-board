package com.example.blog_board.service.file.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import com.example.blog_board.api.file.dto.response.FileResponse;
import com.example.blog_board.domain.file.entity.FileEntity;

@Mapper(
	componentModel = "spring",
	unmappedTargetPolicy = ReportingPolicy.ERROR,
	nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface FileMapper {

	FileResponse toDto(FileEntity file);
}