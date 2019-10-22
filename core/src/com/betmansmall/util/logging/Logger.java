package com.betmansmall.util.logging;

import com.badlogic.gdx.utils.StringBuilder;

import java.text.SimpleDateFormat;
import java.util.HashMap;

/**
 * Logs messages appending tags like:
 *  {caller class name}::{caller method name}
 *  before message.
 *
 * @author Alexander Kuzyakov on 22.04.2019.
 */
public class Logger {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";
    public static Logger instance = new Logger();
    private HashMap<String, String> classNamesCache;
    private String threadClassName;

    private static SimpleDateFormat simpleDateFormat;

    private static String convert(String ... strings) {
        StringBuilder sb = new StringBuilder();
        for (String string : strings) {
            sb.append(string + ",");
        }
        if (sb.length > 0) {
            sb.deleteCharAt(sb.length - 1);
        }
        return sb.toString();
    }

    public static void logFuncStart() {
        instance().log("[START]", ANSI_BLUE);
    }

    public static void logFuncStart(String ... strings) {
        instance().log("[START] -- " + convert(strings), ANSI_BLUE);
    }

    public static void logFuncEnd() {
        instance().log("[END]", ANSI_CYAN);
    }

    public static void logWithTime(String ... strings) {
        instance().log("[" + simpleDateFormat.format(System.currentTimeMillis()) + "] -- " + convert(strings), ANSI_BLACK);
    }

    public static void logError(String message) {
        instance().log(message, ANSI_RED);
    }

    public static void logWarn(String message) {
        instance().log(message, ANSI_YELLOW);
    }

    public static void logInfo(String message) {
        instance().log(message, ANSI_WHITE);
    }

    public static void logDebug(String message) {
        instance().log(message, ANSI_GREEN);
    }

    public static Logger instance() {
        if (instance == null) instance = new Logger();
        return instance;
    }

    public Logger() {
        classNamesCache = new HashMap<String, String>();
        threadClassName = Thread.class.getName();
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss.SSS");//yyyy-MM-dd 'at' HH:mm:ss z");
    }

    /**
     * Appends color symbols to log tag.
     */
    private void log(String message, String color) {
        StackTraceElement callerElement = getCallerElement(Thread.currentThread().getStackTrace());
        if (callerElement == null) return;
        System.out.println(color + getClassName(callerElement) +"::" + callerElement.getMethodName() + "();" + ANSI_RESET + " -- " + message);
    }

    /**
     * Fetches stacktrace and returns caller of method log.
     */
    private StackTraceElement getCallerElement(StackTraceElement[] elements) {
        for (StackTraceElement element : elements) {
            if(element.getClassName().equals(threadClassName)) continue;
            if (!element.getClassName().equals(getClass().getName())) return element;
        }
        return null;
    }

    /**
     * Gets class name without packages from {@link StackTraceElement}.
     * Stores names in cache for faster querying.
     */
    private String getClassName(StackTraceElement element) {
        if (!classNamesCache.containsKey(element.getClassName())) {
            String[] nameParts = element.getClassName().split("\\.");
            classNamesCache.put(element.getClassName(), nameParts[nameParts.length - 1]);
        }
        return classNamesCache.get(element.getClassName());
    }
}
