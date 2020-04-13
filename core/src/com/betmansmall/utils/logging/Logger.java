package com.betmansmall.utils.logging;

import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.StringBuilder;

import org.apache.commons.cli.CommandLine;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;

/**
 * Logs messages appending tags like:
 *  {caller class name}::{caller method name}
 *  before message.
 *
 * @author Alexander Kuzyakov on 22.04.2019.
 */
public class Logger implements Disposable {
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_BLACK = "\u001B[30m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_PURPLE = "\u001B[35m";
    private static final String ANSI_CYAN = "\u001B[36m";
    private static final String ANSI_WHITE = "\u001B[37m";
    private boolean useColors = true;

    private static SimpleDateFormat simpleDateFormat;
    private static Logger instance = new Logger();
    private HashMap<String, String> classNamesCache;
    private String threadClassName;

    private CommandLine cmd;
    private BufferedWriter bufferedWriter;

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

    public static void logFuncEnd(String ... strings) {
        instance().log("[END] -- " + convert(strings), ANSI_CYAN);
    }

    public static void logFuncEnd() {
        instance().log("[END]", ANSI_CYAN);
    }

    public static void logWithTime(String ... strings) {
        instance().log("[" + simpleDateFormat.format(System.currentTimeMillis()) + "] -- " + convert(strings), ANSI_BLACK);
    }

    public static void logError(String ... strings) {
        instance().log(convert(strings), ANSI_RED);
    }

    public static void logWarn(String ... strings) {
        instance().log(convert(strings), ANSI_YELLOW);
    }

    public static void logInfo(String ... strings) {
        instance().log(convert(strings), ANSI_WHITE);
    }

    public static void logDebug(String ... strings) {
        instance().log(convert(strings), ANSI_GREEN);
    }

    public static Logger instance() {
        if (instance == null) instance = new Logger();
        return instance;
    }

    public Logger() {
        classNamesCache = new HashMap<String, String>();
        threadClassName = Thread.class.getName();
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd - HH:mm:ss.SSS");//yyyy-MM-dd 'at' HH:mm:ss z");
    }

    @Override
    public void dispose() {
        if (bufferedWriter != null) {
            try {
                bufferedWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setCmd(CommandLine cmd) {
        this.cmd = cmd;
        try {
            if (cmd.hasOption("server")) {
                this.bufferedWriter = new BufferedWriter(new FileWriter("serverLog.st"));
            } else {
                if (cmd.hasOption("client")) {
                    String value = cmd.getOptionValue("client");
                    if (value != null) {
                        Integer intValue = Integer.parseInt(value);
                        if (intValue != null) {
                            this.bufferedWriter = new BufferedWriter(new FileWriter("client" + intValue + "Log.st"));
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Appends color symbols to log tag.
     */
    private void log(String message, String color) {
        StackTraceElement callerElement = getCallerElement(Thread.currentThread().getStackTrace());
        if (callerElement == null) return;
        String outStr = ((useColors == true) ? color : "") +
                getClassName(callerElement) + "::" + callerElement.getMethodName() + "();" +
                ((useColors == true) ? ANSI_RESET : "") + " -- " + message;
        System.out.println(outStr);
//        System.out.println(color + getClassName(callerElement) + "::" + callerElement.getMethodName() + "();" + ANSI_RESET + " -- " + message);
        if (bufferedWriter != null) {
            try {
                bufferedWriter.write(getClassName(callerElement) + "::" + callerElement.getMethodName() + "(); -- " + message + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
