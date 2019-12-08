package com.github.wuchao.filepreview.enums;

public enum FileFormatEnum {

    PDF("pdf"),

    DOC("doc"),

    DOCX("docx");

    private String name;

    private FileFormatEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
