package com.github.wuchao.filepreview.controller;

import com.github.wuchao.filepreview.enums.FileFormatEnum;
import com.github.wuchao.filepreview.util.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tika.mime.MimeTypeException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 文件预览
 */
@Controller
@RequestMapping("/file")
public class FilePreviewController {

    /**
     * 预览文件
     *
     * @param fileUrl  远程文件地址
     * @param fileName 自定义的文件名称
     * @return
     */
    @GetMapping("/onlinePreview")
    public String onlinePreview(@RequestParam String fileUrl,
                                @RequestParam(required = false, defaultValue = "") String fileName,
                                Model model) {
        String fileExt;
        if (StringUtils.isBlank(fileName) || StringUtils.isBlank(fileExt = FileUtils.getFileExt(fileName))) {
            return "404.html";
        }
        model.addAttribute("fileUrl", "/file/onlinePreviewFile?fileUrl=" + fileUrl
                + "&fileName=" + fileName
                + "&fileExt=" + fileExt);
        if (ArrayUtils.contains(FileUtils.convertToPdfFormats, fileExt)) {
            fileExt = FileFormatEnum.PDF.getName();
        }
        return fileExt.toLowerCase() + "-preview.html";
    }

    /**
     * 预览文件
     *
     * @param fileUrl  远程文件地址
     * @param fileName 自定义的文件名称
     * @param response
     * @return
     * @throws IOException
     * @throws InterruptedException
     * @throws MimeTypeException
     */
    @GetMapping("/onlinePreviewFile")
    @ResponseBody
    public ResponseEntity onlinePreviewFile(@RequestParam String fileUrl,
                                            @RequestParam(required = false, defaultValue = "") String fileName,
                                            HttpServletResponse response) throws IOException, InterruptedException, MimeTypeException {
        FileUtils.previewFile(fileUrl, fileName, response);
        return ResponseEntity.ok().build();
    }

}
