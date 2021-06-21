package com.github.wuchao.filepreview.util;

import com.aspose.cad.Image;
import com.aspose.cad.fileformats.cad.CadDrawTypeMode;
import com.aspose.cad.imageoptions.CadRasterizationOptions;
import com.aspose.cad.imageoptions.PdfOptions;
import com.aspose.cad.imageoptions.PngOptions;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


/**
 * 参考资料：
 * <p>
 * http://cadviewerjs.com/cv-js_api/index_cvjs_24_overview.html
 * <p>
 * https://atom.io/packages/jscad-viewer
 * <p>
 * https://github.com/autodesk-forge/
 * <p>
 * https://sharecad.org/zh/DWGOnlinePlugin
 * <p>
 * http://3d-viewers.com/license.html
 * <p>
 * https://sourceforge.net/projects/js-cad/files/?source=navbar
 * <p>
 * https://github.com/mmik005/js-cad
 * <p>
 * https://github.com/qcad/qcad
 * <p>
 * http://duchangyu.github.io/51CTO/#/25
 * <p>
 * https://github.com/LibreCAD/LibreCAD
 * <p>
 * http://www.qcad.org/en/component/content/article/78-qcad/111-qcad-compilation-from-sources
 * <p>
 * https://github.com/Autodesk-Forge/design.automation-custom-data-viewer
 * <p>
 * Ycad是一款开源的DXF文件解析的工具包，地址为：http://sourceforge.net/projects/ycad
 * <p>
 * Kabeja是一个Java类包用于解析Autodesk的DXF格式并转换成SVG格式(dxf2svg)
 * <p>
 * DWG to PNG:
 * <p>
 * https://github.com/loftuxab/alfresco-vector-transformations-module
 * <p>
 * https://github.com/aspose-cad/Aspose.CAD-for-Java
 */
@Component
public class CADConverter {

    private static final Logger log = LoggerFactory.getLogger(CADConverter.class);

    public static String DWG = "dwg";

    public static String DXF = "dxf";

    public static String DNG = "dng";

    public static String IFC = "ifc";

    public static String STL = "stl";


    /**
     * CAD 文档格式转换（暂时主要以 DWG 格式为主）
     *
     * @param sourcePath   源文件位置
     * @param targetDir    目标文件目录
     * @param targetFormat 目标文件格式
     * @return 转换后的目标文件路径
     */
    public static String convert(String sourcePath, String targetDir, String targetFormat) {
        if (StringUtils.isBlank(sourcePath)) {
            throw new RuntimeException("Error:文件路径不能为空");
        }
        if (StringUtils.isBlank(targetFormat)) {
            throw new RuntimeException("Error:目标类型不能为空");
        }

        if (sourcePath.endsWith(DXF)) {

            return convertByAsposeCad(sourcePath, targetDir, targetFormat);

        } else if (sourcePath.endsWith(DWG)) {

            return convertByAsposeCad(sourcePath, targetDir, targetFormat);

        }

        return null;
    }


    /**
     * 使用 Aspose.CAD-for-Java 转换
     * https://github.com/aspose-cad/Aspose.CAD-for-Java
     *
     * @param sourcePath
     * @param targetDir
     * @param targetFormat
     * @return
     */
    public static String convertByAsposeCad(String sourcePath, String targetDir, String targetFormat) {
        String fileName = FileUtils.getFileName(sourcePath);
        if (fileName == null) {
            throw new RuntimeException("Error:文件路径错误");
        }

        if (StringUtils.isBlank(targetDir)) {
            targetDir = FileUtils.getFileDir(sourcePath);
        }

        // 目标文件名
        String targetFileName = fileName.substring(0, fileName.lastIndexOf(".") + 1) + targetFormat;
        // 转换后的目标文件路径
        String targetPath = targetDir + targetFileName;

        try (Image image = Image.load(sourcePath)) {

            if (ArrayUtils.contains(FileUtils.IMAGE_EXTS, targetFormat)) {

                CadRasterizationOptions cadRasterizationOptions = new CadRasterizationOptions();
                cadRasterizationOptions.setPageHeight(1200);
                cadRasterizationOptions.setPageWidth(1200);
                cadRasterizationOptions.setUnitType(image.getUnitType());
                cadRasterizationOptions.setDrawType(CadDrawTypeMode.UseObjectColor);

                PngOptions options = new PngOptions();
                // 设置 png 的压缩等级，0 最低 9 最高
                options.setCompressionLevel(0);
                options.setVectorRasterizationOptions(cadRasterizationOptions);

                image.save(targetPath, options);

            } else if (FileUtils.PDF_EXT.equalsIgnoreCase(targetFormat)) {

                CadRasterizationOptions cadRasterizationOptions = new CadRasterizationOptions();
                cadRasterizationOptions.setPageWidth(1200);
                cadRasterizationOptions.setPageHeight(1200);
                cadRasterizationOptions.setLayouts(new String[]{"Model"});
                cadRasterizationOptions.setNoScaling(true);
                cadRasterizationOptions.setPdfProductLocation("center");
                cadRasterizationOptions.setAutomaticLayoutsScaling(true);

                PdfOptions options = new PdfOptions();
                options.setVectorRasterizationOptions(cadRasterizationOptions);

                image.save(targetPath, options);
            }

        }

        return targetPath;
    }

}
