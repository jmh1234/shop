package com.example.shop.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

public class LoggerUtil {
    private static volatile Logger logger = null;

    private LoggerUtil() {
    }

    public static Logger getInstance(Class<?> clazz) {
        if (logger == null) {
            synchronized (LoggerUtil.class) {
                if (logger == null) {
                    logger = LoggerFactory.getLogger(clazz);
                }
            }
        }
        return logger;
    }

    /**
     * 异常处理，使日志里能完整的打印出错误信息
     *
     * @param e 异常信息
     * @return String
     */
    public static String formatException(Throwable e) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(bos, true, "UTF-8"));
            return bos.toString("UTF-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
            return null;
        }
    }
}
