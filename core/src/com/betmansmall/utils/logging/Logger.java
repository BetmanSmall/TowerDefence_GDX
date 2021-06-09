package com.betmansmall.utils.logging;

import com.badlogic.gdx.utils.StringBuilder;

import org.apache.commons.cli.CommandLine;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class Logger implements Closeable {
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_BLACK = "\u001b[30;1m"; //"\u001B[30m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_PURPLE = "\u001B[35m";
    private static final String ANSI_CYAN = "\u001B[36m";
    private static final String ANSI_WHITE = "\u001B[37m";
    private boolean useColors = true;

    private static Logger instance;
    private HashMap<String, String> classNamesCache;
    private ArrayList<String> excludeClassNames;
    private SimpleDateFormat simpleDateFormat;

    private CommandLine cmd;
    private boolean useStdOut = true;
    private boolean userWantOutput = false;
    private BufferedWriter bufferedWriter = null;
    private File outputFile = null;
    private File userCustomOutputFile = null;

    private static String convert(String ... strings) {
        StringBuilder sb = new StringBuilder();
        for (String string : strings) {
            sb.append(string + ", ");
        }
        if (sb.length() > 0) {
            sb.delete(sb.length()-2, sb.length());
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
        instance().log("[" + instance().simpleDateFormat.format(System.currentTimeMillis()) + "] -- " + convert(strings), ANSI_BLACK);
    }

    public static void logError(String ... strings) {
        instance().log("[ERROR] -- " + convert(strings), ANSI_RED);
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

    public static void logConsole(String ... strings) {
        ConsoleLoggerTable.log(convert(strings));
        instance().log(convert(strings), ANSI_GREEN);
    }

    public static Logger instance() {
        if (instance == null) instance = new Logger();
        return instance;
    }

    public Logger() {
        classNamesCache = new HashMap<>();
        excludeClassNames = new ArrayList<>();
        excludeClassNames.add(Thread.class.getName());
        excludeClassNames.add(Logger.class.getName());
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd - HH:mm:ss.SSS");//yyyy-MM-dd 'at' HH:mm:ss z");
    }

    @Override
    public void close() {
        this.useStdOut = true;
        logWithTime("Logger.close();");
        closeBufferedWriter();
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
            closeBufferedWriter();
            setUseStdOut(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setUseStdOut(boolean useStdOut) {
        this.useStdOut = useStdOut;
        if (!useStdOut) {
            bufferedWriter = null;
        }
        log("[" + simpleDateFormat.format(System.currentTimeMillis()) + "] -- useStdOut:" + useStdOut, ANSI_PURPLE);
    }

    public void setUserCustomOutputFile(File file) {
        this.userWantOutput = true;
        if (file != null && !file.getPath().isEmpty()) {
            this.userCustomOutputFile = file;
        }
        setFileOutput(userCustomOutputFile);
    }

    public void setFileOutput() {
        if (userWantOutput && userCustomOutputFile == null) {
            setFileOutput(null);
        }
    }

    public void setFileOutput(File fileOutput) {
        if (userWantOutput) {
            File outputFile;
            if (fileOutput != null) {
                outputFile = fileOutput.getParentFile();
                if (outputFile != null) {
                    outputFile.mkdirs();
                }
                outputFile = fileOutput;
            } else {
                String classAndMethodName = getClassAndMethodName();
                classAndMethodName = classAndMethodName.substring(0, classAndMethodName.indexOf("::"));
//                classAndMethodName = classAndMethodName.replaceAll("::", "-");
                File logsDir = new File("logs");
                logsDir.mkdirs();
                outputFile = new File(logsDir + "/" + classAndMethodName + ".log");
            }
            logWarn("change outputFile:" + outputFile);
            closeBufferedWriter();
            try {
                this.bufferedWriter = new BufferedWriter(new FileWriter(outputFile));
                this.outputFile = outputFile;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void closeBufferedWriter() {
        if (bufferedWriter != null) {
            try {
                logFuncEnd("close outputFile:" + outputFile, "bufferedWriter:" + bufferedWriter);
                bufferedWriter.close();
                bufferedWriter = null;
                outputFile = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void log(String message, String color) {
        if (useStdOut || bufferedWriter != null) {
            String classAndMethodName = getClassAndMethodName();
            if (useStdOut) {
                String outStr = ((useColors) ? color : "") + classAndMethodName + ((useColors) ? ANSI_RESET : "") + " -- " + message;
//                outStr = outStr.replace("<init>", className);
                System.out.println(outStr);
            }
            if (bufferedWriter != null) {
                try {
                    bufferedWriter.write(classAndMethodName + " -- " + message + "\n");
                    bufferedWriter.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private StackTraceElement getCallerElement(StackTraceElement[] elements) {
        for (StackTraceElement element : elements) {
            String elementClassName = element.getClassName();
            if (!excludeClassNames.contains(elementClassName)) {
                return element;
            }
        }
        return null;
    }

    private String getClassName(StackTraceElement element) {
        String elementClassName = element.getClassName();
        if (!classNamesCache.containsKey(elementClassName)) {
            classNamesCache.put(elementClassName, elementClassName.substring(elementClassName.lastIndexOf(".") + 1));
        }
        return classNamesCache.get(elementClassName);
    }

    private String getClassAndMethodName() {
        StackTraceElement callerElement = getCallerElement(Thread.currentThread().getStackTrace());
        if (callerElement != null) {
            return getClassName(callerElement) + "::" + callerElement.getMethodName() + "();";
        }
        return "NULL::null();";
//        return getClassName(Objects.requireNonNull(getCallerElement(Thread.currentThread().getStackTrace())));
    }
}
