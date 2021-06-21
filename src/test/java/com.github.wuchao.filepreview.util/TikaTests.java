package com.github.wuchao.filepreview.util;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.dwg.DWGParser;
import org.apache.tika.sax.BodyContentHandler;
import org.junit.Test;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class TikaTests {

    @Test
    public void convertDwgByTika(String sourcePath) {
        // Tika 默认是 10*1024*1024，这里防止文件过大导致 Tika 报错
        BodyContentHandler handler = new BodyContentHandler(1024 * 1024 * 10);
        Metadata metadata = new Metadata();

        try (InputStream inputstream = new FileInputStream(new File(sourcePath));) {
            ParseContext pcontext = new ParseContext();

            // 解析 dwg 文档时应由超类 AbstractParser 的派生类 DWGParser 实现
            Parser dwgParser = new DWGParser();
            dwgParser.parse(inputstream, handler, metadata, pcontext);
            // 获取 dwg 文档的内容
            System.out.println("DWG 文档内容: " + handler.toString());
            // 获取 dwg 文档的元数据
            System.out.println("DWG 文档元数据: ");
            String[] metadataNames = metadata.names();

            for (String name : metadataNames) {
                System.out.println(name + " : " + metadata.get(name));
            }

        } catch (IOException | TikaException | SAXException e) {
            e.printStackTrace();
        }

    }

}
