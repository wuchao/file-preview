package com.github.wuchao.filepreview.util;

import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class FileUtilsTests {

    /**
     * [HTTP Mime-Type 对照表 : Content-Type（Mime-Type） 文件扩展名](http://www.jsons.cn/contenttype/)
     */
    @Test
    public void testGetMimeType() {
        System.out.println("---------------------------------------------------------");
        System.out.println("pdf：" + FileUtils.getMimeType("pdf"));
        System.out.println(".pdf: " + FileUtils.getMimeType(".pdf"));
        System.out.println("http://www.baidu.com/111.pdf：" + FileUtils.getMimeType("http://www.baidu.com/111.pdf"));
        System.out.println("D:/aaa.doc：" + FileUtils.getMimeType("D:/aaa.doc"));
        System.out.println("bbb.docx：" + FileUtils.getMimeType("bbb.docx"));
        System.out.println(".doc：" + FileUtils.getMimeType(".doc"));
        System.out.println(".docx：" + FileUtils.getMimeType(".docx"));
        System.out.println(".ppt：" + FileUtils.getMimeType(".ppt"));
        System.out.println(".pptx：" + FileUtils.getMimeType(".pptx"));
        System.out.println(".xls：" + FileUtils.getMimeType(".xls"));
        System.out.println(".xlsx：" + FileUtils.getMimeType(".xlsx"));
        System.out.println(".txt：" + FileUtils.getMimeType(".txt"));
        System.out.println(".js：" + FileUtils.getMimeType(".js"));
        System.out.println(".css：" + FileUtils.getMimeType(".css"));
        System.out.println(".html：" + FileUtils.getMimeType(".html"));
        System.out.println(".xhtml：" + FileUtils.getMimeType(".xhtml"));
        System.out.println(".xml：" + FileUtils.getMimeType(".xml"));
        System.out.println(".jpeg：" + FileUtils.getMimeType(".jpeg"));
        System.out.println(".jpg：" + FileUtils.getMimeType(".jpg"));
        System.out.println(".png：" + FileUtils.getMimeType(".png"));
        System.out.println(".mp4：" + FileUtils.getMimeType(".mp4"));
        System.out.println(".mp3：" + FileUtils.getMimeType(".mp3"));

        /**
         * 打印结果：
         * pdf：application/pdf
         * .pdf: application/pdf
         * http://www.baidu.com/111.pdf：null
         * D:/aaa.doc：application/msword
         * bbb.docx：application/vnd.openxmlformats-officedocument.wordprocessingml.document
         * .doc：application/msword
         * .docx：application/vnd.openxmlformats-officedocument.wordprocessingml.document
         * .ppt：application/vnd.ms-powerpoint
         * .pptx：application/vnd.openxmlformats-officedocument.presentationml.presentation
         * .xls：application/vnd.ms-excel
         * .xlsx：application/vnd.openxmlformats-officedocument.spreadsheetml.sheet
         * .txt：text/plain
         * .js：application/x-javascript
         * .css：text/css
         * .html：text/html
         * .xhtml：text/html
         * .xml：text/xml
         * .jpeg：image/jpeg
         * .jpg：image/jpeg
         * .png：image/png
         * .mp4：video/mpeg4
         * .mp3：audio/mp3
         */
    }

    @Test
    public void testFileExt() {
        System.out.println(FileUtils.getFileExt("D:/aaa.doc"));
        System.out.println(FileUtils.getFileExt("bbb.docx"));
        System.out.println(FileUtils.getFileExt("http://www.baidu.com/111.pdf"));

        /**
         * 打印结果：
         * doc
         * docx
         * pdf
         */
    }

    @Test
    public void testGetFileExtFromUrl() {
        System.out.println(FileUtils.getFileExtFromUrl("D:/aaa.doc"));
        System.out.println(FileUtils.getFileExtFromUrl("bbb.docx"));
        System.out.println(FileUtils.getFileExtFromUrl("http://www.baidu.com/111.pdf"));
        System.out.println(FileUtils.getFileExtFromUrl("http://www.baidu.com/111.pdf?filename=111.pdf"));
        System.out.println(FileUtils.getFileExtFromUrl("http://www.baidu.com/downloadFile"));
        System.out.println(FileUtils.getFileExtFromUrl("http://www.baidu.com/downloadFile?filename=111.pdf"));

        /**
         * 打印结果：
         * doc
         * null
         * pdf
         * pdf
         * null
         * pdf
         */
    }

    @Test
    public void testGetTempDir() {
        System.out.println(System.getProperty("java.io.tmpdir"));
        System.out.println(System.getProperty("user.dir"));

        /**
         * 打印结果：
         *
         * Windows：
         * C:\Users\ecidi\AppData\Local\Temp\
         * D:\IdeaProjects\wuchao\file-preview
         *
         */
    }


    @Test
    public void testFile() throws IOException {
        File file = new File("D:\\IdeaProjects\\wuchao\\file-preview\\src\\test\\java\\com.github.wuchao.filepreview.util\\FileUtilsTests.java");
        System.out.println(file.getName());
        System.out.println(file.getAbsolutePath());
        System.out.println(file.getCanonicalPath());
        System.out.println(file.getParent());
        System.out.println(file.getPath());

        /**
         * 执行结果：
         *
         * FileUtilsTests.java
         * D:\IdeaProjects\wuchao\file-preview\src\test\java\com.github.wuchao.filepreview.util\FileUtilsTests.java
         * D:\IdeaProjects\wuchao\file-preview\src\test\java\com.github.wuchao.filepreview.util\FileUtilsTests.java
         * D:\IdeaProjects\wuchao\file-preview\src\test\java\com.github.wuchao.filepreview.util
         * D:\IdeaProjects\wuchao\file-preview\src\test\java\com.github.wuchao.filepreview.util\FileUtilsTests.java
         */
    }

}
