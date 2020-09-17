package com.github.wuchao.filepreview.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

public abstract class CommandUtils {

    private static final Logger log = LoggerFactory.getLogger(CommandUtils.class);

    private CommandUtils() {
    }

    /**
     * 执行操作系统命令
     *
     * @param command
     */
    public static void execCommand(String command) throws IOException, InterruptedException {
        log.info("execute file format convert command: {}", command);
        String osName = System.getProperty("os.name");
        ProcessBuilder builder;

        if (osName.contains("Windows")) {
            builder = new ProcessBuilder(
                    "cmd.exe", "/c", command);
        } else if (osName.contains("Linux")) {
            builder = new ProcessBuilder(
                    "sh", "-c", command);
        } else {
            throw new RuntimeException("暂时只测试过 Windows 和 Linux 服务器");
        }

        // 执行命令, 返回一个子进程对象（命令在子进程中执行）
        Process process = builder
                // 方法告诉此进程生成器是否合并标准错误和标准输出。
                // 如果此属性为 true，则通过子进程所产生的任何错误输出将与标准输出合并，
                // 合并的数据可从 Process.getInputStream() 返回的流读取。
                .redirectErrorStream(true)
                .start();

        try (InputStream fis = process.getInputStream();
             // 添加 GBK 是解决下面 log.info(line) 输出中文乱码的问题
             InputStreamReader isr = new InputStreamReader(fis, "GBK");
             BufferedReader br = new BufferedReader(isr);) {
            String line;
            while ((line = br.readLine()) != null) {
                log.info(line);
            }
        }

        // 调用 waitFor 方法，主进程会等待子进程的命令执行完成，成功会返回 0
        process.waitFor(10, TimeUnit.SECONDS);
    }

}
