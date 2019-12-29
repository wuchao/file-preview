package com.github.wuchao.filepreview.util;

import com.github.wuchao.filepreview.common.Constants;
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
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;

public abstract class FileUtils {

    private static final Logger log = LoggerFactory.getLogger(FileUtils.class);

    private FileUtils() {
    }

    /**
     * 需要转换成 PDF 格式预览的常见文档格式
     */
    public static String[] convertToPdfExtensions = {"doc", "docx", "xls", "xlsx", "ppt", "pptx"};

    /**
     * 常见图片格式
     */
    public static String[] imageExtensions = {
            "bmp", "jpg", "jpeg", "png", "gif"
    };

    /**
     * 常见压缩格式
     */
    public static String[] compressExtensions = {
            "zip", "rar", "7z", "tar", "jar"
    };

    /**
     * 预览时，是否需要转换源文件格式，如果不需要则直接用流传输到前端（即文件下载）
     *
     * @param fileExt
     * @return
     */
    public static boolean shouldConvertPreviewFileFormat(String fileExt) {
        if (ArrayUtils.contains(convertToPdfExtensions, fileExt)) {
            return true;
        }
        return false;
    }

    /**
     * 预览文件
     *
     * @param fileUrl  远程文件地址
     * @param fileName 自定义的文件名称
     * @param response
     */
    public static void previewFile(String fileUrl, String fileName, HttpServletResponse response) throws IOException, InterruptedException, MimeTypeException {
        downloadFileFromURLUsingNIO(fileUrl, fileName, true, response);
    }

    /**
     * @param filePath 源文件地址
     * @return 转换后的目标文件地址
     * @throws IOException
     * @throws InterruptedException
     */
    public static String convertFileFormat(String filePath) throws IOException, InterruptedException {
        String fileExt = getFileExt(filePath);
        if (ArrayUtils.contains(convertToPdfExtensions, fileExt)) {
            // 转换文件
            return OfficeConverter.convertOfficeByLibreOffice(filePath, null, Constants.FILE_EXT_PDF);
        } else {
            return filePath;
        }
    }

    /*private static void downloadOrPreviewFile(String fileName, boolean previewFile, InputStream inputStream, HttpServletResponse response) throws IOException, InterruptedException {
        if (previewFile && ArrayUtils.contains(convertToPdfFormats, getFileExt(fileName))) {
            // 预览文件
            String targetFilepath = convertFileFormat(inputStream, getMimeType(fileName), fileName);
            downloadFileFromLocalSystem(targetFilepath, null, true, response);
        } else {
            // 下载文件
            setResponse(response, fileName, previewFile);
            copy(inputStream, response.getOutputStream());
        }
    }*/


    /**
     * 下载文件（NIO）
     * [Download a File From an URL in Java](https://www.baeldung.com/java-download-file)
     *
     * @param fileUrl     远程文件地址
     * @param fileName    自定义的文件名称
     * @param previewFile
     * @param response
     */
    public static void downloadFileFromURLUsingNIO(String fileUrl,
                                                   String fileName,
                                                   boolean previewFile,
                                                   HttpServletResponse response) throws IOException, InterruptedException, MimeTypeException {

        if (StringUtils.isNotBlank(fileUrl)) {
            // 文件拓展名
            String fileExt;

            if (StringUtils.isNotBlank(fileName)) {
                fileExt = getFileExt(fileName);
                if (StringUtils.isBlank(fileExt)) {
                    fileName += ("." + fileExt);
                }
            } else {
                fileExt = getFileExt(fileUrl);
                if (StringUtils.isNotBlank(fileExt)) {
                    fileName = getFileName(fileUrl);
                }
            }

            // URL 中没有文件名称和拓展名，用 URLConnection 下载预览
            if (StringUtils.isBlank(getFileExt(fileUrl))) {
                downloadFileFromURLUsingURLConn(fileUrl, fileName, previewFile, response);
                return;
            }

            String tmpdir = getTempDir();
            String filePath = tmpdir + fileName;
            ReadableByteChannel readChannel = null;
            FileChannel writeChannel = null;

            try (InputStream urlInputStream = new URL(fileUrl).openStream();
                 FileOutputStream fileOutputStream = new FileOutputStream(filePath)) {
                readChannel = Channels.newChannel(urlInputStream);
                writeChannel = fileOutputStream.getChannel();

                long size = writeChannel.transferFrom(readChannel, 0, Long.MAX_VALUE);

                if (size > 0) {
                    if (previewFile && shouldConvertPreviewFileFormat(fileExt)) {
                        // 预览文件

                        log.info("download file from URL using NIO.");
                        String targetFilepath = convertFileFormat(filePath);
                        downloadFileFromLocalSystem(targetFilepath, null, true, response);

                    } else {
                        // 下载文件

                        try (InputStream inputStream = new FileInputStream(filePath)) {
                            setResponse(response, fileName, previewFile);
                            copy(inputStream, response.getOutputStream());
                        }
                    }
                }
            } catch (Exception e) {
                if (e instanceof FileNotFoundException) {
                    log.error("找不到指定文件");
                } else {
                    log.error(e.getMessage());
                }
                throw e;
            } finally {
                if (writeChannel != null) {
                    writeChannel.close();
                }
                if (readChannel != null) {
                    readChannel.close();
                }
            }
        }
    }

    /**
     * 下载文件（URLConnection）
     * [Java HttpURLConnection to download file from an HTTP URL](https://www.codejava.net/java-se/networking/use-httpurlconnection-to-download-file-from-an-http-url)
     *
     * @param fileUrl     远程文件地址
     * @param fileName    自定义的文件名称
     * @param previewFile
     * @param response
     */
    public static void downloadFileFromURLUsingURLConn(String fileUrl,
                                                       String fileName,
                                                       boolean previewFile,
                                                       HttpServletResponse response) throws IOException, InterruptedException, MimeTypeException {

        if (StringUtils.isNotBlank(fileUrl)) {
            URL url = new URL(fileUrl);
            URLConnection conn = url.openConnection();
            // 设置超时间为 10 秒
            conn.setConnectTimeout(10000);
            // 防止屏蔽程序抓取而返回403错误
            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

            // 文件名
            if (StringUtils.isBlank(fileName)) {
                if (fileUrl.lastIndexOf(".") > 0) {
                    fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
                }
                if (StringUtils.isBlank(fileName) && conn.getHeaderField("Content-Disposition") != null) {
                    String contentDisposition = new String(conn
                            .getHeaderField("Content-Disposition")
                            .getBytes("ISO-8859-1"), "GBK");
                    fileName = contentDisposition.substring(contentDisposition.indexOf("filename=") + ("filename=".length()));
                }
            }

            // 文件拓展名
            String fileExt = getFileExtByMimeType(conn.getContentType());
            if (StringUtils.isBlank(fileExt)) {
                fileExt = getFileExt(fileName);
            }

            try (InputStream inputStream = conn.getInputStream()) {
                if (previewFile && shouldConvertPreviewFileFormat(fileExt)) {
                    // 预览文件

                    log.info("download file from URL using URLConnection.");
                    String tmpdir = getTempDir();
                    String filePath = tmpdir + fileName;

                    // 下载文件到本地
                    try (FileOutputStream outputStream = new FileOutputStream(filePath);
                         BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream)) {
                        copy(inputStream, bufferedOutputStream);
                    }
                    // 转换文件格式
                    String targetFilepath = convertFileFormat(filePath);
                    // 预览文件
                    downloadFileFromLocalSystem(targetFilepath, null, true, response);

                } else {
                    // 下载文件

                    setResponse(response, fileName, previewFile);
                    copy(inputStream, response.getOutputStream());
                }
            }
        }
    }

    /**
     * 下载本地系统文件
     *
     * @param filePath
     * @param fileName
     * @param previewFile 是否预览文件
     * @param response
     */
    public static void downloadFileFromLocalSystem(String filePath,
                                                   String fileName,
                                                   boolean previewFile,
                                                   HttpServletResponse response) throws IOException, InterruptedException {

        if (StringUtils.isNotBlank(filePath)) {
            // 文件拓展名
            String fileExt = getFileExt(filePath);

            // 文件名
            if (StringUtils.isBlank(fileName)) {
                fileName = filePath.substring(filePath.lastIndexOf(File.separator) + 1);
            } else {
                if (!fileName.contains(".")
                        || !fileName.substring(fileName.lastIndexOf(".") + 1).equalsIgnoreCase(fileExt)) {
                    fileName += ("." + fileExt);
                }
            }

            if (previewFile && shouldConvertPreviewFileFormat(fileExt)) {
                // 预览文件

                String targetFilepath = convertFileFormat(filePath);
                downloadFileFromLocalSystem(targetFilepath, null, true, response);

            } else {
                // 下载文件

                try (InputStream inputStream = new FileInputStream(filePath)) {
                    setResponse(response, processFileName(fileName), previewFile);
                    copy(inputStream, response.getOutputStream());
                }
            }
        }
    }

    /**
     * 从 path 或 URL 中获取文件名称
     *
     * @param filePath
     * @return
     */
    public static String getFileName(String filePath) {
        if (filePath.lastIndexOf("/") >= 0) {
            return filePath.substring(filePath.lastIndexOf("/") + 1);
        } else if (filePath.lastIndexOf("\\") >= 0) {
            return filePath.substring(filePath.lastIndexOf("\\") + 1);
        }
        return null;
    }

    /**
     * 获取文件拓展名
     *
     * @param filePath
     * @return
     */
    public static String getFileExt(String filePath) {
        return StringUtils.isNotBlank(filePath)
                ? filePath.substring(filePath.lastIndexOf(".") + 1).toLowerCase()
                : null;
    }

    /**
     * 获取 mimeType
     *
     * @param fileName
     * @return
     */
    public static String getMimeType(String fileName) {
        return URLConnection.getFileNameMap().getContentTypeFor(fileName);
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
     * @param fileName
     * @return
     */
    public static String processFileName(String fileName) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        if (request != null) {
            String userAgent = request.getHeader("USER-AGENT");
            try {
                if (userAgent.toUpperCase().indexOf("MSIE") > 0) {
                    fileName = URLEncoder.encode(fileName, "UTF-8");
                } else {
                    // Google Chrome 浏览器使用 fileName = URLEncoder.encode(fileName, "UTF-8");
                    fileName = new String(fileName.getBytes("UTF-8"), "ISO8859-1");
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return fileName;
    }

    /**
     * 设置文件下载时的请求响应流
     *
     * @param response
     * @param fileName
     * @param previewFile
     */
    public static void setResponse(HttpServletResponse response, String fileName, boolean previewFile) {
        if (previewFile) {
            // 预览文件

            String mimeType = getMimeType(fileName);
            response.setContentType(mimeType + ";charset=UTF-8");

        } else {
            // 下载文件

            response.setCharacterEncoding("UTF-8");
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        }
    }

    /**
     * 获取临时文件目录
     *
     * @return
     */
    public static String getTempDir() {
//        String tmpdir = System.getProperty("java.io.tmpdir");
        String tmpdir = System.getProperty("user.dir") + File.separator;
        return tmpdir;
    }

    /**
     * 输入流转输出流
     *
     * @param inputStream
     * @param outputStream
     * @throws IOException
     */
    public static void copy(InputStream inputStream, OutputStream outputStream) throws IOException {
        int i = IOUtils.copy(inputStream, outputStream);
        if (i == -1) {
            IOUtils.copyLarge(inputStream, outputStream);
        }
    }

}
