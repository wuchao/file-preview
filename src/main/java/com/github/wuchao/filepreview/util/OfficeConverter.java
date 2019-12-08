package com.github.wuchao.filepreview.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Slf4j
@Component
public class OfficeConverter {

    /**
     * LibreOffice 的 bin 目录
     */
    private static String LIBRE_OFFICE_BIN_FILE_PATH;

    @Value("${libreoffice.bin.file-path}")
    public void setLibreOfficeBinFilePath(String libreOfficeBinFilePath) {
        OfficeConverter.LIBRE_OFFICE_BIN_FILE_PATH = libreOfficeBinFilePath;
    }

    /**
     * word（doc/docx） 转 pdf
     * C:\Program Files\LibreOffice\program>soffice.bin --headless --invisible --convert-to pdf C:\Users\xx.doc --outdir C:\Users
     *
     * @param docPath 源文件位置
     * @return
     */
    public static String word2PdfByLibreOffice(String docPath) throws IOException, InterruptedException {
        return convertOfficeByLibreOffice(docPath, docPath.substring(0, docPath.lastIndexOf(File.separator)), "pdf");
    }

    /**
     * doc 转 docx
     * C:\Program Files\LibreOffice\program>soffice.bin --headless --invisible --convert-to docx C:\Users\xx.doc --outdir C:\Users
     *
     * @param docPath 源文件位置
     * @return
     */
    public static String doc2DocxByLibreOffice(String docPath) throws IOException, InterruptedException {
        return convertOfficeByLibreOffice(docPath, docPath.substring(0, docPath.lastIndexOf(File.separator)), "docx");
    }

    /**
     * Office 文档格式转换
     *
     * @param docPath      源文件位置
     * @param targetDir    目标文件目录
     * @param targetFormat 目标文件格式
     * @return
     */
    public static String convertOfficeByLibreOffice(String docPath, String targetDir, String targetFormat) throws IOException, InterruptedException {
        if (StringUtils.isBlank(docPath) || docPath.contains(" ")) {
            return "Error:word 文件名不能包含空格";
        }

        int fileFileSeparatorIndex = docPath.lastIndexOf(File.separator);

        // 设置文件转换输出目录
        if (StringUtils.isBlank(targetDir)) {
            targetDir = docPath.substring(0, fileFileSeparatorIndex);
        }

        StringBuilder command = new StringBuilder()
                .append("\"")
                .append(LIBRE_OFFICE_BIN_FILE_PATH)
                .append("\"")
                .append(" --headless --invisible --convert-to ")
                .append(targetFormat).append(" ").append(docPath)
                .append(" --outdir ").append(targetDir);

        // 执行文档转换命令
        CommandUtils.execCommand(command.toString());

        // 返回转换后的目标文件的文件路径
        return new StringBuilder()
                .append(targetDir)
                .append(targetDir.endsWith(File.separator) ? "" : File.separator)
                .append(docPath.substring(fileFileSeparatorIndex + 1).replace(docPath.substring(docPath.lastIndexOf('.') + 1), targetFormat))
                .toString();
    }

}
