package com.sky.web.admin;

import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/admin/common")
public class CommonController {
    @Autowired
    private AliOssUtil aliOssUtil;

    @PostMapping("upload")
    public Result upload(MultipartFile file) throws IOException {
        String upload = aliOssUtil.upload(file.getOriginalFilename(), file.getInputStream());
        return Result.success(upload);
    }
}
