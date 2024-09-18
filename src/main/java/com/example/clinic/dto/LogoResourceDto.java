package com.example.clinic.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

@Getter
@RequiredArgsConstructor
public final class LogoResourceDto {

    private final Resource resource;
    private final String contentType;

    private LogoResourceDto(MultipartFile file) {
        resource = file.getResource();
        contentType = file.getContentType();
    }

    public static LogoResourceDto of(MultipartFile file) {
        return file == null ? null : new LogoResourceDto(file);
    }
}