package com.github.wuchao.filepreview.util;

import org.junit.Test;

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
        System.out.println(".java：" + FileUtils.getMimeType(".java"));
        System.out.println(".jsp：" + FileUtils.getMimeType(".jsp"));
        System.out.println(".js：" + FileUtils.getMimeType(".js"));
        System.out.println(".css：" + FileUtils.getMimeType(".css"));
        System.out.println(".html：" + FileUtils.getMimeType(".html"));
        System.out.println(".xhtml：" + FileUtils.getMimeType(".xhtml"));
        System.out.println(".xml：" + FileUtils.getMimeType(".xml"));
        System.out.println(".md：" + FileUtils.getMimeType(".md"));
        System.out.println(".jpeg：" + FileUtils.getMimeType(".jpeg"));
        System.out.println(".jpg：" + FileUtils.getMimeType(".jpg"));
        System.out.println(".png：" + FileUtils.getMimeType(".png"));
        System.out.println(".mp4：" + FileUtils.getMimeType(".mp4"));
        System.out.println(".mp3：" + FileUtils.getMimeType(".mp3"));

        /**
         * 打印结果：
         * pdf：null
         * .pdf: application/pdf
         * http://www.baidu.com/111.pdf：application/pdf
         * D:/aaa.doc：null
         * bbb.docx：null
         * .doc：null
         * .docx：null
         * .ppt：null
         * .pptx：null
         * .xls：null
         * .xlsx：null
         * .txt：text/plain
         * .java：text/plain
         * .jsp：null
         * .js：null
         * .css：null
         * .html：text/html
         * .xhtml：null
         * .xml：application/xml
         * .md：null
         * .jpeg：image/jpeg
         * .jpg：image/jpeg
         * .png：image/png
         * .mp4：null
         * .mp3：null
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
