package com.github.wuchao.filepreview.util;

public abstract class SystemPropertyUtil {

    /**
     * https://stackoverflow.com/questions/228477/how-do-i-programmatically-determine-operating-system-in-java
     */
    public enum OS {
        WINDOWS, LINUX, UNIX
    }

    /**
     * Operating Systems.
     */
    public static OS os = null;

    /**
     * 获取操作系统类型
     *
     * @return
     */
    public static OS getOS() {
        if (os == null) {
            String osName = System.getProperty("os.name").toLowerCase();
            if (osName.contains("win")) {
                os = OS.WINDOWS;
            } else if (osName.contains("linux")) {
                os = OS.LINUX;
            } else if (osName.contains("nix")
                    || osName.contains("aix")
                    || osName.contains("mac")
                    || osName.contains("sunos")) {
                os = OS.UNIX;
            }
        }
        return os;
    }


    /**
     * 获取系统临时目录
     *
     * @return
     */
    public static String getTempDir() {
        return System.getProperty("java.io.tmpdir");
    }

}
