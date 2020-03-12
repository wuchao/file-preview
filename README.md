# file-preview

文件在线预览，支持预览格式：pdf、doc/docx、xsl/xslx、ppt/pptx、txt、图片(png、jpg、jpeg、gif...)、压缩文件（zip、tar、7z...）。

预览方式（以本地启动为例）：

http://localhost:8088/onlinePreview?fileUrl=https://gitee.com/wu726/web-project/raw/master/core/DocxResume.docx&fileName=DocxResume.docx

- fileUrl：远程文件地址（required = true）
- fileName：自定义的下载文件名称，要带拓展名（required = false）

### PDF 前段预览插件
[PDFObject](https://pdfobject.com/)（使用）

[PDF.js](http://mozilla.github.io/pdf.js/getting_started/)


### 压缩文件预览插件
[jszip](https://github.com/Stuk/jszip)（使用）

[zip.js](https://github.com/gildas-lormeau/zip.js)