package com.github.wuchao.filepreview.util;

import org.apache.commons.lang3.ArrayUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.regex.Pattern;

public class HttpUtils {

    /**
     * 匹配中文的正则表达式
     */
    private final static Pattern CHINESE_PATTERN = Pattern.compile("[\u4e00-\u9fa5]");


    /**
     * 对 url 链接进行编码，避免带中文的情况
     *
     * @param url
     * @return
     */
    public static String encodeUrl(String url) {
        // 对中文参数进行编码
        // 第一个 / 的索引
        int firstSplitIndex = url.indexOf('/', url.indexOf("://") + 3);
        if (firstSplitIndex > 0) {
            // 第一个 ? 的索引
            int questionMarkIndex;
            if ((questionMarkIndex = url.indexOf('?')) > 0 && questionMarkIndex > firstSplitIndex) {

            } else {
                questionMarkIndex = url.length();
            }

            String[] urls = url.substring(firstSplitIndex + 1, questionMarkIndex).split("/");

            if (ArrayUtils.isNotEmpty(urls)) {
                StringBuilder urlBuilder = new StringBuilder()
                        .append(url, 0, firstSplitIndex);
                Arrays.stream(urls).forEach(str -> {
                    try {
                        if (CHINESE_PATTERN.matcher(str).find()) {
                            urlBuilder.append('/').append(URLEncoder.encode(str, "utf-8"));
                        } else {
                            urlBuilder.append('/').append(str);
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                });

                // 将 ? 及其后面的参数拼接上
                if (questionMarkIndex != url.length()) {
                    urlBuilder.append(url.substring(questionMarkIndex));
                }

                url = urlBuilder.toString();
            }
        }
        System.out.println(url);
        return url;
    }

}
