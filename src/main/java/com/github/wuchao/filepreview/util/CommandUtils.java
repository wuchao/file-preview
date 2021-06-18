package com.github.wuchao.filepreview.util;

import org.apache.commons.lang3.SystemUtils;
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
     * 生成 ProcessBuilder
     *
     * @param command
     * @return
     */
    static ProcessBuilder getProcessBuilder(String command) {
        ProcessBuilder processBuilder;

        if (SystemUtils.IS_OS_WINDOWS) {
            processBuilder = new ProcessBuilder("cmd.exe", "/c", command);
        } else if (SystemUtils.IS_OS_LINUX) {
            processBuilder = new ProcessBuilder("sh", "-c", command);
        } else {
            throw new RuntimeException("暂时只测试过 Windows 和 Linux 服务器");
        }

        // 方法告诉此进程生成器是否合并标准错误和标准输出
        // 如果此属性为 true，则通过子进程所产生的任何错误输出将与标准输出合并
        // 合并的数据可从 Process.getInputStream() 返回的流读取
        processBuilder.redirectErrorStream(true);

        return processBuilder;
    }


    /**
     * 执行命令
     *
     * @param command
     */
    public static void execCommand(String command) throws IOException, InterruptedException {
        log.info("execute file format convert command: {}", command);

        // 执行命令, 返回一个子进程对象（命令在子进程中执行）
        Process process = getProcessBuilder(command).start();

        try (InputStream fis = process.getInputStream();
             // 添加 GBK 是解决下面 log.info(line) 输出中文乱码的问题
             InputStreamReader isr = new InputStreamReader(fis, "GBK");
             BufferedReader br = new BufferedReader(isr)) {

            String line;
            while ((line = br.readLine()) != null) {
                log.info(line);
            }
        }

        // 调用 waitFor 方法，主进程会等待子进程的命令执行完成，成功会返回 true
        process.waitFor(10, TimeUnit.SECONDS);

        // 杀死子进程
        process.destroy();
        // 休眠 1s
        Thread.sleep(1000);
        // 判断 process 代表的子进程是否存活
        if (process.isAlive()) {
            // 强制杀死子进程
            process.destroyForcibly();
        }

        /**
         * jdk9 之后杀死进程可以这样：
         * ProcessHandle handle = p.toHandle();
         * handle.destroy();
         * handle.descendants().forEach(ProcessHandle::destroy);
         */
    }


}
