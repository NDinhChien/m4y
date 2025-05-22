package com.ndinhchien.m4y.global.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.ndinhchien.m4y.domain.user.entity.User;
import com.ndinhchien.m4y.global.exception.BusinessException;
import com.ndinhchien.m4y.global.exception.ErrorMessage;

@Component
public class SystemFileService {
    private static final String IMAGE_FOLDER = "images";
    private static final List<String> IMAGE_ACCEPTED_TYPES = Arrays.asList(
            "image/jpeg", "image/png");
    private static final Integer IMAGE_MAX_SIZE = 2 * 1024 * 1024;

    @Value("${spring.web.resources.static-locations}")
    private String[] staticFolders;

    public String saveAvatar(User user, MultipartFile avatar) throws BusinessException {
        validateAvatar(avatar);
        try {
            Path imageFolder = getImageFolder();
            if (!Files.exists(imageFolder)) {
                Files.createDirectories(imageFolder);
            }

            String fileName = user.getUserName() + getExtention(avatar.getOriginalFilename());
            Path filePath = imageFolder.resolve(fileName);
            avatar.transferTo(filePath);
            return fileName;
        } catch (Exception ex) {
            throw new BusinessException("Failed to update avatar");
        }
    }

    private Path getImageFolder() {
        return Paths.get(getPublicFolder(), IMAGE_FOLDER);
    }

    private String getPublicFolder() {
        for (String folder : staticFolders) {
            if (folder.startsWith("file:")) {
                return folder.substring(5);
            }
        }
        return "./public/";

    }

    private String getExtention(String originalFileName) {
        int lastIndex = originalFileName.lastIndexOf(".");
        if (lastIndex >= 0)
            return originalFileName.substring(lastIndex);
        return "";
    }

    private void validateAvatar(MultipartFile avatar) throws BusinessException {
        if (!IMAGE_ACCEPTED_TYPES.contains(avatar.getContentType())) {
            throw new BusinessException(HttpStatus.FORBIDDEN, ErrorMessage.UNSUPPORTED_FILE_TYPE);
        }
        if (avatar.getSize() > IMAGE_MAX_SIZE) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, ErrorMessage.FILE_SIZE_EXCEEDED);
        }
    }

}
