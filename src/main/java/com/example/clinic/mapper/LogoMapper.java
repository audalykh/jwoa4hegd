package com.example.clinic.mapper;

import com.example.clinic.model.Logo;
import java.io.IOException;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.springframework.web.multipart.MultipartFile;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface LogoMapper {

    @Mapping(source = "originalFilename", target = "name")
    @Mapping(source = "bytes", target = "data")
    @Mapping(source = "contentType", target = "type")
    Logo toEntity(MultipartFile file) throws IOException;

    @Mapping(source = "originalFilename", target = "name")
    @Mapping(source = "bytes", target = "data")
    @Mapping(source = "contentType", target = "type")
    Logo toEntity(MultipartFile file, @MappingTarget Logo logo) throws IOException;
}