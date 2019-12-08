package com.github.wuchao.filepreview.controller;

import com.github.wuchao.filepreview.util.FileUtils;
import org.apache.tika.mime.MimeTypeException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 文件下载
 */
@RestController
@RequestMapping("/file")
public class FileDownloadController {

    /**
     * 下载文件
     *
     * @param fileUrl  远程文件地址
     * @param filename 自定义的文件名称
     * @param response
     * @return
     * @throws IOException
     * @throws InterruptedException
     * @throws MimeTypeException
     */
    @GetMapping("/download")
    public ResponseEntity filePreview(@RequestParam String fileUrl,
                                      @RequestParam(required = false, defaultValue = "") String filename,
                                      HttpServletResponse response) throws IOException, InterruptedException, MimeTypeException {
        FileUtils.downloadFile(fileUrl, filename, false, response);
        return ResponseEntity.ok().build();
    }

}
