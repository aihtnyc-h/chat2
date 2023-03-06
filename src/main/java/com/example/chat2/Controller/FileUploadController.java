package com.example.chat2.Controller;

import com.example.chat2.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
public class FileUploadController {

    private final FileUploadService fileUploadService;

    // @RequestPart 어노테이션을 이용해서 multipart/form-data 요청을 받을 수 있습니다.
    // MultipartFile을 통해 비즈니스 로직은 서비스 로직으로 위임하겠습니다.
    @PostMapping("/api/upload")
    public String uploadImage(@RequestPart MultipartFile file) {
        return fileUploadService.uploadImage(file);
    }
    // 서비스로부터 URI을 받아서 클라이언트로 반환합니다.

}