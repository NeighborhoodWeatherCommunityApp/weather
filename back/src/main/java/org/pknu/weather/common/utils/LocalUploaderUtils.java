package org.pknu.weather.common.utils;

import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.pknu.weather.apipayload.code.status.ErrorStatus;
import org.pknu.weather.exception.GeneralException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Component
@Slf4j
public class LocalUploaderUtils {

    @Value("${spring.servlet.multipart.location}")
    private String uploadPath;

    public String uploadLocal(MultipartFile multipartFile) {

        if (multipartFile == null || multipartFile.isEmpty()){
            throw new GeneralException(ErrorStatus._INTERNAL_SERVER_ERROR);
        }

        String uuid = UUID.randomUUID().toString();
        String saveFileName = uuid + "_" +multipartFile.getOriginalFilename();

        Path savePath = Paths.get(uploadPath, saveFileName);

        try {
            Thumbnails.of(multipartFile.getInputStream())
                    .size(320, 320)
                    .toFile(savePath.toFile());
        } catch (IOException e ){
            log.error("프로필 이미지 저장 실패: " + e.getMessage());
            throw new GeneralException(ErrorStatus._INTERNAL_SERVER_ERROR);
        }

        return savePath.toAbsolutePath().toString();
    }
}
