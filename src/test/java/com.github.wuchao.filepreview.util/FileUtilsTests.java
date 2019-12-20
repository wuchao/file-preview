package com.github.wuchao.filepreview.util;

import org.junit.Test;

public class FileUtilsTests {

    /**
     * [HTTP Mime-Type 对照表 : Content-Type（Mime-Type） 文件扩展名](http://www.jsons.cn/contenttype/)
     */
    @Test
    public void testGetMimeType() {
        System.out.println("---------------------------------------------------------");
        System.out.println(FileUtils.getMimeType("pdf"));
        System.out.println(FileUtils.getMimeType(".pdf"));
        System.out.println(FileUtils.getMimeType("http://www.baidu.com/111.pdf"));
        System.out.println(FileUtils.getMimeType("D:/aaa.doc"));
        System.out.println(FileUtils.getMimeType("bbb.docx"));
        System.out.println(FileUtils.getMimeType(".doc"));
        System.out.println(FileUtils.getMimeType(".docx"));
        System.out.println(FileUtils.getMimeType(".ppt"));
        System.out.println(FileUtils.getMimeType(".pptx"));
        System.out.println(FileUtils.getMimeType(".xls"));
        System.out.println(FileUtils.getMimeType(".xlsx"));
        System.out.println(FileUtils.getMimeType(".java"));
        System.out.println(FileUtils.getMimeType(".jsp"));
        System.out.println(FileUtils.getMimeType(".js"));
        System.out.println(FileUtils.getMimeType(".css"));
        System.out.println(FileUtils.getMimeType(".html"));
        System.out.println(FileUtils.getMimeType(".xhtml"));
        System.out.println(FileUtils.getMimeType(".xml"));
        System.out.println(FileUtils.getMimeType(".jpeg"));
        System.out.println(FileUtils.getMimeType(".jpg"));
        System.out.println(FileUtils.getMimeType(".png"));
        System.out.println(FileUtils.getMimeType(".mp4"));
        System.out.println(FileUtils.getMimeType(".mp3"));

        /**
         * 打印结果：
         * null
         * application/pdf
         * application/pdf
         * null
         * null
         * null
         * null
         * null
         * null
         * null
         * null
         * text/plain
         * null
         * null
         * null
         * text/html
         * null
         * application/xml
         * image/jpeg
         * image/jpeg
         * image/png
         * null
         * null
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

}
