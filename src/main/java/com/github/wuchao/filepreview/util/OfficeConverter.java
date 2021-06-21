package com.github.wuchao.filepreview.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Component
public class OfficeConverter {

    private static final Logger log = LoggerFactory.getLogger(OfficeConverter.class);

    /**
     * LibreOffice 的 office 文档转 pdf 文档命令
     */
    private static String office2PDFCommand;

    @Value("${libre-office.convert-command.office-to-pdf}")
    public void setOffice2PDFCommand(String office2PDFCommand) {
        OfficeConverter.office2PDFCommand = office2PDFCommand;
    }

    /**
     * Office 文档格式转换
     * <p>
     * 已验证的操作：
     * doc 转 docx
     * doc/docx 转 pdf
     * <p>
     * xls 转 xlsx
     * xls/xlsx 转 pdf
     * <p>
     * ppt 转 pptx
     * ppt/pptx 转 pdf
     *
     * @param sourcePath   源文件位置
     * @param targetDir    目标文件目录
     * @param targetFormat 目标文件格式
     * @return 转换后的目标文件地址
     * @throws IOException
     * @throws InterruptedException
     */
    public static String convert(String sourcePath, String targetDir, String targetFormat) throws IOException, InterruptedException {
        if (StringUtils.isBlank(sourcePath) || sourcePath.contains(" ")) {
            throw new RuntimeException("Error:文件名不能为空且不能包含空格");
        }
        if (StringUtils.isBlank(targetFormat)) {
            throw new RuntimeException("Error:目标类型不能为空");
        }

        // 源文件拓展名
        String sourceFileExt = FileUtils.getFileExt(sourcePath);
        if (StringUtils.isBlank(sourceFileExt)) {
            throw new RuntimeException("Error:文件地址错误");
        }

        int fileFileLastSeparatorIndex = sourcePath.lastIndexOf(File.separator);

        // 设置文件转换输出目录
        if (StringUtils.isBlank(targetDir)) {
            targetDir = sourcePath.substring(0, fileFileLastSeparatorIndex);
        }

        // libreoffice 文档格式转换命令
        String command = String.format(office2PDFCommand, targetFormat, sourcePath, targetDir);

        if (StringUtils.isNotBlank(command)) {
            log.info(command);

            // 执行文档转换命令
            CommandUtils.execCommand(command);

            // 返回转换后的目标文件路径
            return new StringBuilder()
                    .append(targetDir)
                    // 检查是否需要拼接路径分隔符
                    .append(targetDir.endsWith(File.separator) ? "" : File.separator)
                    // 替换文件拓展名
                    .append(sourcePath
                            .substring(fileFileLastSeparatorIndex + 1)
                            .replace(sourcePath.substring(sourcePath.lastIndexOf('.') + 1), targetFormat))
                    .toString();
        }

        return Strings.EMPTY;
    }

}
