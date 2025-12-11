package org.example.backend.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    /**
     * 保存文件并返回可访问 URL
     * @param file 上传的文件
     * @return 文件访问 URL
     */
    String saveFile(MultipartFile file);
}
