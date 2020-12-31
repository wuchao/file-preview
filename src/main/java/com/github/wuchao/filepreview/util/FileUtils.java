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
     * 所有可预览的文档格式
     */
    public static String[] previewExtensions = {
            // pdf
            "pdf",
            // convertToPdfExtensions
            "doc", "docx", "xls", "xlsx", "ppt", "pptx",
            // imageExtensions
            "bmp", "jpg", "jpeg", "png", "gif",
            // compressExtensions
            "zip", "rar", "7z", "tar", "jar"
    };

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
    public static void previewFile(String fileUrl, String fileName, HttpServletResponse response) throws IOException, InterruptedException {
        if (StringUtils.isNotBlank(fileName)) {
            // 取消文件名称中的空格
            fileName = fileName.trim().replace(" ", "");
        }
        previewFileUsingNIO(fileUrl, fileName, response);
    }


    /**
     * 预览文件（NIO）
     * [Download a File From an URL in Java](https://www.baeldung.com/java-download-file)
     *
     * @param fileUrl  远程文件地址
     * @param fileName 自定义的文件名称
     * @param response
     */
    public static void previewFileUsingNIO(String fileUrl,
                                           String fileName,
                                           HttpServletResponse response) throws IOException, InterruptedException {

        if (StringUtils.isNotBlank(fileUrl) && StringUtils.isNotBlank(fileName)) {

            String filePath = SystemPropertyUtil.getTempDir() + fileName;
            ReadableByteChannel readChannel = null;
            FileChannel writeChannel = null;

            // 对文件 url 链接进行编码，避免文件 url 带中文的情况
            fileUrl = HttpUtils.encodeUrl(fileUrl);

            try (InputStream urlInputStream = new URL(fileUrl).openStream();
                 FileOutputStream fileOutputStream = new FileOutputStream(filePath)) {

                readChannel = Channels.newChannel(urlInputStream);
                writeChannel = fileOutputStream.getChannel();

                long size = writeChannel.transferFrom(readChannel, 0, Long.MAX_VALUE);

                if (size > 0) {
                    log.info("download file from URL using NIO.");
                    downloadFileFromLocalSystem(filePath, fileName, true, response);
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
                if (StringUtils.isNotBlank(filePath)) {
                    // 删除源文件
                    org.apache.commons.io.FileUtils.deleteQuietly(new File(filePath));
                }
            }
        }
    }


    /**
     * 预览文件（URLConnection）
     * [Java HttpURLConnection to download file from an HTTP URL](https://www.codejava.net/java-se/networking/use-httpurlconnection-to-download-file-from-an-http-url)
     *
     * @param fileUrl  远程文件地址
     * @param fileName 自定义的文件名称
     * @param response
     */
    public static void previewFileUsingURLConn(String fileUrl,
                                               String fileName,
                                               HttpServletResponse response) throws IOException, InterruptedException {

        if (StringUtils.isNotBlank(fileUrl) && StringUtils.isNotBlank(fileName)) {
            // 对文件 url 链接进行编码，避免文件 url 带中文的情况
            fileUrl = HttpUtils.encodeUrl(fileUrl);
            URL url = new URL(fileUrl);
            URLConnection conn = url.openConnection();
            // 设置超时间为 60 秒
            conn.setConnectTimeout(60000);
            conn.setReadTimeout(60000);
            // 防止屏蔽程序抓取而返回403错误
            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

            try (InputStream inputStream = conn.getInputStream()) {

                log.info("download file from URL using URLConnection.");

                String filePath = SystemPropertyUtil.getTempDir() + fileName;

                // 下载文件到本地
                try (FileOutputStream outputStream = new FileOutputStream(filePath);
                     BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream)) {

                    copy(inputStream, bufferedOutputStream);

                    // 预览文件
                    downloadFileFromLocalSystem(filePath, fileName, true, response);

                } finally {

                    // 删除源文件
                    if (StringUtils.isNotBlank(filePath)) {
                        org.apache.commons.io.FileUtils.deleteQuietly(new File(filePath));
                    }
                }

            }
        }
    }


    /**
     * 转换文件格式
     *
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


    /**
     * 从服务器下载文件到浏览器
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
            log.info("文件地址：{}", filePath);

            // 文件拓展名
            String fileExt = getFileExt(filePath);

            if (previewFile && shouldConvertPreviewFileFormat(fileExt)) {
                // 预览文件

                // 转换文件格式
                String targetFilepath = convertFileFormat(filePath);

                try {
                    downloadFileFromLocalSystem(targetFilepath, null, true, response);
                } finally {
                    // 删除目标文件
                    if (StringUtils.isNotBlank(targetFilepath)) {
                        org.apache.commons.io.FileUtils.deleteQuietly(new File(targetFilepath));
                    }
                }

            } else {
                // 下载文件

                // 文件名
                if (StringUtils.isBlank(fileName)) {
                    fileName = filePath.substring(filePath.lastIndexOf(File.separator) + 1);
                }

                try (InputStream inputStream = new FileInputStream(filePath)) {
                    setResponse(response, encodeFileName(fileName), previewFile);
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
        if (StringUtils.isNotBlank(filePath)) {
            if (filePath.indexOf(".") >= 0) {
                return filePath.substring(filePath.lastIndexOf(".") + 1).toLowerCase();
            }
        }
        return null;
    }


    /**
     * 根据下载链接获取要下载文件的拓展名
     *
     * @param fileDownloadUrl
     * @return
     */
    public static String getFileExtFromUrl(String fileDownloadUrl) {
        if (StringUtils.isNotBlank(fileDownloadUrl)) {

            int lastPathSeparatorIndex;
            int lastDotIndex;
            int questionMarkIndex;
            String subString;
            String fileExt;

            if (fileDownloadUrl.indexOf("?") > 0) {
                questionMarkIndex = fileDownloadUrl.indexOf("?");
            } else {
                questionMarkIndex = fileDownloadUrl.length();
            }

            // 从 url 域名后的最后一个 / 开始截取，有 ? 截取到 ? 前面一个字符截至，没有 ? 就截取到 url 结尾处
            // 截取后的字符串中查找最后一个 . 后面的字符串
            // xxx.com/111.pdf
            // xxx.com/download?filename=111.pdf
            if ((lastPathSeparatorIndex = fileDownloadUrl.lastIndexOf('/')) > 0
                    && (lastDotIndex = (subString = fileDownloadUrl.substring(lastPathSeparatorIndex, questionMarkIndex)).lastIndexOf(".")) > 0) {

                fileExt = subString.substring(lastDotIndex + 1);

                if (fileExt != null) {
                    fileExt = fileExt.toLowerCase();

                    if (ArrayUtils.contains(FileUtils.previewExtensions, fileExt)) {
                        return fileExt;
                    }
                }
            }

            // 域名后面没有找到拓展名，则将 ? 后面的字符串按照 & 分割成数组，查看每个参数里面是否能找到拓展名
            if (questionMarkIndex < fileDownloadUrl.length()) {
                String[] substrings = fileDownloadUrl.substring(questionMarkIndex).split("&");
                for (String substr : substrings) {
                    if ((lastDotIndex = substr.lastIndexOf('.')) > 0
                            && ArrayUtils.contains(previewExtensions, (fileExt = substr.substring(lastDotIndex + 1).toLowerCase()))) {
                        return fileExt;
                    }
                }
            }

            return null;
        }

        return null;
    }


    /**
     * 获取 mimeType
     *
     * @param fileName
     * @return
     */
    public static String getMimeType(String fileName) {
        if (StringUtils.isNotBlank(fileName)) {
            int index;
            if ((index = fileName.lastIndexOf('.')) >= 0) {
                return FileExtensionsUtil.fileExtensionMap.get(fileName.substring(index + 1));
            } else {
                return FileExtensionsUtil.fileExtensionMap.get(fileName);
            }
        }
        return null;
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
    public static String encodeFileName(String fileName) {
        if (StringUtils.isNotBlank(fileName)) {
            try {
                fileName = URLEncoder.encode(fileName, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return fileName;
    }


    /**
     * 处理文件名称，防止乱码
     *
     * @param fileName
     * @return
     */
    public static String encodeFileName2(String fileName) {
        if (StringUtils.isNotBlank(fileName)) {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            if (request != null) {
                String userAgent = request.getHeader("USER-AGENT");
                try {
                    userAgent = userAgent.toLowerCase();
                    /*if (userAgent.contains("msie")) {
                        // IE Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; rv:11.0) like Gecko
                        fileName = URLEncoder.encode(fileName, "UTF-8");
                    } else*/
                    if (userAgent.contains("firefox")) {
                        // 火狐 Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:76.0) Gecko/20100101 Firefox/76.0
                        fileName = URLEncoder.encode(fileName, "UTF-8");

                    } else if (userAgent.contains("chrome")) {
                        // Google:Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36 Edg/81.0.416.77
                        // 也可以写成：fileName = new String(fileName.getBytes("UTF-8"), "ISO8859-1");
                        fileName = URLEncoder.encode(fileName, "UTF-8");
                    } else {
                        fileName = URLEncoder.encode(fileName, "UTF-8");
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
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
