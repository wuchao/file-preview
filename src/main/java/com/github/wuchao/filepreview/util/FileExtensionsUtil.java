package com.github.wuchao.filepreview.util;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * https://stackoverflow.com/questions/27402992/java-a-way-to-match-mime-content-type-to-file-extension-from-commonsmultipart
 */
public abstract class FileExtensionsUtil {

    public static final Map<String, String> fileExtensionMap;

    static {
        fileExtensionMap = new HashMap<>();

        // MS Office
        fileExtensionMap.put("doc", "application/msword");
        fileExtensionMap.put("dot", "application/msword");
        fileExtensionMap.put("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        fileExtensionMap.put("dotx", "application/vnd.openxmlformats-officedocument.wordprocessingml.template");
        fileExtensionMap.put("docm", "application/vnd.ms-word.document.macroEnabled.12");
        fileExtensionMap.put("dotm", "application/vnd.ms-word.template.macroEnabled.12");
        fileExtensionMap.put("xls", "application/vnd.ms-excel");
        fileExtensionMap.put("xlt", "application/vnd.ms-excel");
        fileExtensionMap.put("xla", "application/vnd.ms-excel");
        fileExtensionMap.put("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        fileExtensionMap.put("xltx", "application/vnd.openxmlformats-officedocument.spreadsheetml.template");
        fileExtensionMap.put("xlsm", "application/vnd.ms-excel.sheet.macroEnabled.12");
        fileExtensionMap.put("xltm", "application/vnd.ms-excel.template.macroEnabled.12");
        fileExtensionMap.put("xlam", "application/vnd.ms-excel.addin.macroEnabled.12");
        fileExtensionMap.put("xlsb", "application/vnd.ms-excel.sheet.binary.macroEnabled.12");
        fileExtensionMap.put("ppt", "application/vnd.ms-powerpoint");
        fileExtensionMap.put("pot", "application/vnd.ms-powerpoint");
        fileExtensionMap.put("pps", "application/vnd.ms-powerpoint");
        fileExtensionMap.put("ppa", "application/vnd.ms-powerpoint");
        fileExtensionMap.put("pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation");
        fileExtensionMap.put("potx", "application/vnd.openxmlformats-officedocument.presentationml.template");
        fileExtensionMap.put("ppsx", "application/vnd.openxmlformats-officedocument.presentationml.slideshow");
        fileExtensionMap.put("ppam", "application/vnd.ms-powerpoint.addin.macroEnabled.12");
        fileExtensionMap.put("pptm", "application/vnd.ms-powerpoint.presentation.macroEnabled.12");
        fileExtensionMap.put("potm", "application/vnd.ms-powerpoint.presentation.macroEnabled.12");
        fileExtensionMap.put("ppsm", "application/vnd.ms-powerpoint.slideshow.macroEnabled.12");

        // Open Office
        fileExtensionMap.put("odt", "application/vnd.oasis.opendocument.text");
        fileExtensionMap.put("ott", "application/vnd.oasis.opendocument.text-template");
        fileExtensionMap.put("oth", "application/vnd.oasis.opendocument.text-web");
        fileExtensionMap.put("odm", "application/vnd.oasis.opendocument.text-master");
        fileExtensionMap.put("odg", "application/vnd.oasis.opendocument.graphics");
        fileExtensionMap.put("otg", "application/vnd.oasis.opendocument.graphics-template");
        fileExtensionMap.put("odp", "application/vnd.oasis.opendocument.presentation");
        fileExtensionMap.put("otp", "application/vnd.oasis.opendocument.presentation-template");
        fileExtensionMap.put("ods", "application/vnd.oasis.opendocument.spreadsheet");
        fileExtensionMap.put("ots", "application/vnd.oasis.opendocument.spreadsheet-template");
        fileExtensionMap.put("odc", "application/vnd.oasis.opendocument.chart");
        fileExtensionMap.put("odf", "application/vnd.oasis.opendocument.formula");
        fileExtensionMap.put("odb", "application/vnd.oasis.opendocument.database");
        fileExtensionMap.put("odi", "application/vnd.oasis.opendocument.image");
        fileExtensionMap.put("oxt", "application/vnd.openofficeorg.extension");

        // Other
        fileExtensionMap.put("txt", "text/plain");
        fileExtensionMap.put("rtf", "application/rtf");
        fileExtensionMap.put("pdf", "application/pdf");
        fileExtensionMap.put("js", "application/x-javascript");
        fileExtensionMap.put("css", "text/css");
        fileExtensionMap.put("html", "text/html");
        fileExtensionMap.put("xhtml", "text/html");
        fileExtensionMap.put("xml", "text/xml");
        fileExtensionMap.put("sh", "text/x-shellscript");
        fileExtensionMap.put("py", "text/x-python");
        fileExtensionMap.put("java", "text/x-java-source");
        fileExtensionMap.put("sql", "text/x-sql");
        fileExtensionMap.put("php", "text/x-php");

        // Image
        fileExtensionMap.put("jpg", "image/jpeg");
        fileExtensionMap.put("jpeg", "image/jpeg");
        fileExtensionMap.put("png", "image/png");
        fileExtensionMap.put("bmp", "image/x-ms-bmp");
        fileExtensionMap.put("ico", "image/icon");
        fileExtensionMap.put("gif", "image/gif");
        fileExtensionMap.put("tif", "image/tiff");
        // image/psd、image/photoshop、image/x-photoshot
        fileExtensionMap.put("psd", "image/vnd.adobe.photoshop");
        fileExtensionMap.put("psb", "image/vnd.adobe.photoshop");
        fileExtensionMap.put("pdd", "image/vnd.adobe.photoshop");
        fileExtensionMap.put("pcx", "image/pcx");
//        fileExtensionMap.put("pxr", "image/vnd.adobe.photoshop");
//        fileExtensionMap.put("raw", "image/vnd.adobe.photoshop");
        fileExtensionMap.put("ps", "application/postscript");
        fileExtensionMap.put("eps", "application/postscript");
        fileExtensionMap.put("dxf", "image/vnd-dxf");
        fileExtensionMap.put("dwg", "image/vnd-dwg");
        fileExtensionMap.put("dwt", "image/vnd-dwg");
        fileExtensionMap.put("tga", "image/x-targa");
        fileExtensionMap.put("xpm", "image/x-xpm");
        fileExtensionMap.put("svg", "image/svg+xml");
        fileExtensionMap.put("wmf", "image/x-wmf");
        fileExtensionMap.put("cgm", "image/cgm");
        fileExtensionMap.put("cpi", "image/cpi");
        fileExtensionMap.put("cpc", "image/cpi");

        // Video
        fileExtensionMap.put("mp4", "video/mpeg4");
        fileExtensionMap.put("avi", "video/x-msvideo");
        fileExtensionMap.put("dv", "video/x-dv");
        fileExtensionMap.put("mpeg", "video/mpeg");
        fileExtensionMap.put("mpg", "video/mpeg");
        fileExtensionMap.put("mov", "video/quicktime");
        fileExtensionMap.put("wma", "video/wma");
        fileExtensionMap.put("mts", "video/vnd.dlna.mpeg-tts");
        fileExtensionMap.put("webm", "video/webm");
        fileExtensionMap.put("wmv", "video/x-ms-wmv");
        fileExtensionMap.put("flv", "video/x-flv");
        fileExtensionMap.put("mkv", "video/x-matroska");
        fileExtensionMap.put("3gp", "video/3gpp");
        fileExtensionMap.put("vob", "video/vob");

        // Audio
        fileExtensionMap.put("mp3", "audio/mp3");
        fileExtensionMap.put("mid", "audio/midi");
        fileExtensionMap.put("ogg", "audio/ogg");
        fileExtensionMap.put("mp4a", "audio/mp4");
        fileExtensionMap.put("wav", "audio/wav");
        fileExtensionMap.put("wma", "audio/x-ms-wma");

        // Compressed File
        fileExtensionMap.put("zip", "application/zip");
        fileExtensionMap.put("rar", "application/x-rar-compressed");
        fileExtensionMap.put("z", "application/x-compress");
        fileExtensionMap.put("7z", "application/x-7z-compressed");
        fileExtensionMap.put("tar", "application/x-tar");
        fileExtensionMap.put("gz", "application/x-gzip");
        fileExtensionMap.put("tgz", "application/x-gtar");
        fileExtensionMap.put("gtar", "application/x-gtar");
        fileExtensionMap.put("bz", "application/x-bzip2");
        fileExtensionMap.put("bz2", "application/x-bzip2");
        fileExtensionMap.put("tbz", "application/x-bzip2");
        fileExtensionMap.put("jar", "application/java-archive");

        fileExtensionMap.put("gzip", "application/java-archive");

    }

    public static String getExt(String contentType) {
        if (StringUtils.isNotBlank(contentType)) {
            for (Map.Entry t : FileExtensionsUtil.fileExtensionMap.entrySet()) {
                if (contentType.equals(t.getValue())) {
                    return (String) t.getKey();
                }
            }
        }
        return null;
    }

}
