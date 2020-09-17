package com.github.wuchao.filepreview.controller;

import com.github.wuchao.filepreview.common.Constants;
import com.github.wuchao.filepreview.util.FileUtils;
import com.github.wuchao.filepreview.util.OfficeConverter;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tika.mime.MimeTypeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 文件预览
 */
@Controller
public class FilePreviewController {

    private static final Logger log = LoggerFactory.getLogger(OfficeConverter.class);


    /**
     * 预览文件
     *
     * @param fileUrl  远程文件地址
     * @param fileName 远程文件名称
     * @return
     */
    @GetMapping("/onlinePreview")
    public String fileOnlinePreview(@RequestParam String fileUrl,
                                    @RequestParam String fileName,
                                    Model model) {

        String fileExt = FileUtils.getFileExt(fileName);

        if (!ArrayUtils.contains(FileUtils.previewExtensions, fileExt)) {
            return "404";
        }

        fileExt = fileExt.toLowerCase();
        String viewTemplatePrefix = null;
        if (Constants.FILE_EXT_PDF.equalsIgnoreCase(fileExt)
                || ArrayUtils.contains(FileUtils.convertToPdfExtensions, fileExt)) {
            // 预览 pdf 文件
            viewTemplatePrefix = "pdf";
        } else if (Constants.FILE_EXT_TXT.equalsIgnoreCase(fileExt)) {
            // 预览 txt 纯文本文件
            viewTemplatePrefix = fileExt;
        } else if (ArrayUtils.contains(FileUtils.imageExtensions, fileExt)) {
            // 预览图片文件
            viewTemplatePrefix = "image";
        } else if (ArrayUtils.contains(FileUtils.compressExtensions, fileExt)) {
            // 预览压缩文件
            viewTemplatePrefix = "compress";
        }

        if (StringUtils.isNotBlank(viewTemplatePrefix)) {
            model.addAttribute("fileUrl",
                    "/previewFile?fileUrl=" + fileUrl
                            + "&fileName=" + fileName);

            return viewTemplatePrefix + "-preview";
        }

        return "404";
    }


    /**
     * 预览文件
     *
     * @param fileUrl  远程文件地址
     * @param fileName 远程文件名称
     * @param response
     * @return
     * @throws IOException
     * @throws InterruptedException
     * @throws MimeTypeException
     */
    @GetMapping("/previewFile")
    @ResponseBody
    public void previewFile(@RequestParam String fileUrl,
                            @RequestParam String fileName,
                            HttpServletResponse response) {
        try {
            FileUtils.previewFile(fileUrl, fileName, response);
        } catch (Exception e) {
            try {
                log.error(e.getMessage());
                if (e.getMessage().contains("response code: 403")) {
                    response.sendRedirect("/403");
                } else {
                    response.sendRedirect("/500");
                }
            } catch (IOException ex) {
                log.error(ex.getMessage());
            }
        }
    }

}
