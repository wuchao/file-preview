package com.github.wuchao.filepreview.controller;

import com.github.wuchao.filepreview.util.FileUtils;
import org.apache.tika.mime.MimeTypeException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 文件下载
 */
@RestController
public class FileDownloadController {

    /**
     * 下载文件
     *
     * @param fileUrl  远程文件地址
     * @param fileName 自定义的文件名称
     * @param response
     * @return
     * @throws IOException
     * @throws InterruptedException
     * @throws MimeTypeException
     */
    @GetMapping("/download")
    public ResponseEntity filePreview(@RequestParam String fileUrl,
                                      @RequestParam(required = false, defaultValue = "") String fileName,
                                      @RequestParam(required = false, defaultValue = "") String fileExt,
                                      HttpServletResponse response) throws IOException, InterruptedException, MimeTypeException {
        FileUtils.downloadFileFromURLUsingNIO(fileUrl, fileName, false, response);
        return ResponseEntity.ok().build();
    }

}
