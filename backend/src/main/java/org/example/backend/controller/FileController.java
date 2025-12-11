package org.example.backend.controller;

import org.example.backend.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/file")
public class FileController {

    @Autowired
    private FileService fileService;

    // 后端访问前端可用的基础 URL
    private final String baseUrl = "http://localhost:8080/uploads/";

    @PostMapping("/upload")
    public Map<String, Object> upload(@RequestParam("file") MultipartFile file) {
        // saveFile 返回的是保存的文件名，不是绝对路径
        String filename = fileService.saveFile(file);

        Map<String, Object> res = new HashMap<>();
        res.put("code", 200);
        res.put("msg", "上传成功");
        res.put("data", baseUrl + filename);  // 拼成完整 URL 返回前端

        return res;
    }
}
