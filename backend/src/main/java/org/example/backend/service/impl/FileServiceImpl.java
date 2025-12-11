package org.example.backend.service.impl;

import org.example.backend.service.FileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service
public class FileServiceImpl implements FileService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    public String saveFile(MultipartFile multipartFile) {
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String originalFilename = multipartFile.getOriginalFilename();
        String filename = System.currentTimeMillis() + "_" + originalFilename;

        File dest = new File(dir, filename);

        try {
            multipartFile.transferTo(dest);
        } catch (IOException e) {
            throw new RuntimeException("文件保存失败", e);
        }

        // 返回前端可访问的 URL（假设前端访问路径为 /uploads/**）
        return filename;
    }
}
