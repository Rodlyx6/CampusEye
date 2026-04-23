package org.example.backend.controller;

import org.example.backend.common.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/file")
public class FileController {

    @Value("${file.upload-path}")
    private String uploadPath;

    @PostMapping("/upload")
    public Result<String> upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("上传文件不能为空");
        }
        String fileUrl = saveSingleFile(file);
        if (fileUrl == null) {
            return Result.error("文件上传失败");
        }
        return Result.success("上传成功", fileUrl);
    }

    @PostMapping("/uploadBatch")
    public Result<List<String>> uploadBatch(@RequestParam("files") MultipartFile[] files) {
        if (files == null || files.length == 0) {
            return Result.error("上传文件不能为空");
        }

        List<String> urls = new ArrayList<>();
        for (MultipartFile file : files) {
            if (file == null || file.isEmpty()) {
                continue;
            }
            String fileUrl = saveSingleFile(file);
            if (fileUrl != null) {
                urls.add(fileUrl);
            }
        }

        if (urls.isEmpty()) {
            return Result.error("文件上传失败");
        }
        return Result.success("批量上传成功", urls);
    }

    private String saveSingleFile(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String ext = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            ext = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String newFilename = UUID.randomUUID().toString().replace("-", "") + ext;

        File dir = new File(uploadPath);
        if (!dir.exists() && !dir.mkdirs()) {
            return null;
        }

        try {
            file.transferTo(new File(dir, newFilename));
            // 返回相对于前端assets目录的路径，确保能被正确访问
            return "/images/" + newFilename;
        } catch (IOException e) {
            return null;
        }
    }
}
