package com.github.wuchao.filepreview.util;

import com.github.wuchao.filepreview.enums.FileFormatEnum;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public abstract class FileUtils {

    private static final Logger log = LoggerFactory.getLogger(FileUtils.class);

    private FileUtils() {
    }

    /**
     * 文档格式
     */
    public static String[] fileFormats = {"pdf", "doc", "docx",};

    /**
     * 需要转换成 PDF 格式预览的文档格式
     */
    public static String[] convertToPdfFormats = {"doc", "docx",};

    /**
     * 预览文件
     *
     * @param fileUrl  远程文件地址
     * @param filename 自定义的文件名称
     * @param response
     */
    public static void previewFile(String fileUrl, String filename, HttpServletResponse response) throws IOException, InterruptedException, MimeTypeException {
        downloadFile(fileUrl, filename, true, response);
    }

    /**
     * @param inputStream
     * @param contentType
     * @param filename
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public static String convertFileFormat(InputStream inputStream, String contentType, String filename) throws IOException, InterruptedException {
//        String tmpdir = System.getProperty("java.io.tmpdir");
        String tmpdir = System.getProperty("user.dir") + File.separator;
        String filepath = tmpdir + filename;
        try (FileOutputStream outputStream = new FileOutputStream(filepath)) {
            // 下载文件到本地
            IOUtils.copy(inputStream, outputStream);
            // 转换文件
            return OfficeConverter.convertOfficeByLibreOffice(filepath, null, FileFormatEnum.PDF.getName());
        }
    }

    /**
     * 下载文件
     *
     * @param fileUrl     远程文件地址
     * @param filename    自定义的文件名称
     * @param previewFile
     * @param response
     */
    public static void downloadFile(String fileUrl, String filename, boolean previewFile, HttpServletResponse response) throws IOException, InterruptedException, MimeTypeException {
        if (StringUtils.isNotBlank(fileUrl)) {
            URL url = new URL(fileUrl);
            URLConnection conn = url.openConnection();
            // 设置超时间为 10 秒
            conn.setConnectTimeout(10000);
            // 防止屏蔽程序抓取而返回403错误
            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

            String fileExt = getFileExtByMimeType(conn.getContentType());
            if (StringUtils.isNotBlank(filename)) {
                if (!filename.contains(".")
                        || !filename.substring(filename.lastIndexOf(".") + 1).equalsIgnoreCase(fileExt)) {
                    filename += ("." + fileExt);
                }
            } else {
                if (ArrayUtils.contains(fileFormats, fileUrl.substring(fileUrl.lastIndexOf(".") + 1))) {
                    filename = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
                }
                if (StringUtils.isBlank(filename) && conn.getHeaderField("Content-Disposition") != null) {
                    String contentDisposition = new String(conn
                            .getHeaderField("Content-Disposition")
                            .getBytes("ISO-8859-1"), "GBK");
                    filename = contentDisposition.substring(contentDisposition.indexOf("filename=") + ("filename=".length()));
                }
            }

            try (InputStream inputStream = conn.getInputStream()) {
                if (previewFile && ArrayUtils.contains(convertToPdfFormats, fileExt)) {
                    // 预览文件
                    String targetFilepath = convertFileFormat(inputStream, conn.getContentType(), filename);
                    downloadSystemFile(targetFilepath, null, true, response);
                } else {
                    // 下载文件
                    setResponse(response, filename, previewFile);
                    IOUtils.copy(inputStream, response.getOutputStream());
                }
            }
        }
    }

    /**
     * 下载本地系统文件
     *
     * @param filepath
     * @param filename
     * @param response
     */
    public static void downloadSystemFile(String filepath, String filename, boolean previewFile, HttpServletResponse response) throws IOException, InterruptedException {
        if (StringUtils.isNotBlank(filepath)) {
            try (InputStream inputStream = new FileInputStream(filepath)) {
                // 文件拓展名
                String fileExt = getFileExt(filepath);

                // 文件名
                if (StringUtils.isBlank(filename)) {
                    filename = filepath.substring(filepath.lastIndexOf(File.separator) + 1);
                } else {
                    if (!filename.contains(".")
                            || !filename.substring(filename.lastIndexOf(".") + 1).equalsIgnoreCase(fileExt)) {
                        filename += ("." + fileExt);
                    }
                }

                if (previewFile && ArrayUtils.contains(convertToPdfFormats, fileExt)) {
                    // 预览文件
                    String targetFilepath = convertFileFormat(inputStream, FileUtils.getMimeType(filepath), filename);
                    downloadSystemFile(targetFilepath, null, true, response);
                } else {
                    // 下载文件
                    setResponse(response, processFileName(filename), previewFile);
                    IOUtils.copy(inputStream, response.getOutputStream());
                }
            }
        }
    }

    /**
     * 获取文件拓展名
     *
     * @param filepath
     * @return
     */
    public static String getFileExt(String filepath) {
        return StringUtils.isNotBlank(filepath)
                ? filepath.substring(filepath.lastIndexOf(".") + 1).toLowerCase()
                : null;
    }

    /**
     * 获取 mimeType
     *
     * @param fileUrl
     * @return
     */
    public static String getMimeType(String fileUrl) {
        return URLConnection.getFileNameMap().getContentTypeFor(fileUrl);
    }

    /**
     * 根据 mimeType 获取文档拓展名
     *
     * @param mimeType
     * @return
     * @throws MimeTypeException
     */
    public static String getFileExtByMimeType(String mimeType) throws MimeTypeException {
        MimeType mt = MimeTypes.getDefaultMimeTypes().forName(mimeType);
        return mt != null && StringUtils.isNotBlank(mt.getExtension()) ? mt.getExtension().substring(1) : null;
    }

    /**
     * 处理文件名称，防止乱码
     *
     * @param filename
     * @return
     */
    public static String processFileName(String filename) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        if (request != null) {
            String userAgent = request.getHeader("USER-AGENT");
            try {
                if (userAgent.toUpperCase().indexOf("MSIE") > 0) {
                    filename = URLEncoder.encode(filename, "UTF-8");
                } else {
                    // Google Chrome 浏览器使用 fileName = URLEncoder.encode(fileName, "UTF-8");
                    filename = new String(filename.getBytes("UTF-8"), "ISO8859-1");
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return filename;
    }

    /**
     * 设置文件下载时的请求响应流
     *
     * @param response
     * @param filename
     * @param previewFile
     */
    public static void setResponse(HttpServletResponse response, String filename, boolean previewFile) {
        if (previewFile) {
            // 预览文件

            response.reset();
            response.setContentType("application/pdf;charset=UTF-8");

        } else {
            // 下载文件

            response.setCharacterEncoding("UTF-8");
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            response.setHeader("Content-Disposition", "attachment;filename=" + filename);
        }
    }

}
