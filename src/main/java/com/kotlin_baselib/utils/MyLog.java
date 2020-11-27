package com.kotlin_baselib.utils;

import android.text.TextUtils;
import android.util.Log;

import com.kotlin_baselib.BuildConfig;

/**
 * 日志打印工具，全名方法{@link #info(String, String)}、{@link #debug(String, String)}
 * {@link #warn(String, String)}、{@link #error(String, String)}会打印整个调用栈，
 * 缩写方法默认打印{@value #DEFAULT_STACK}个栈信息
 * <p/>
 * Create by Luokaijian on 2019/1/2 <br>
 * Email: luokj@healthmall.cn
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class MyLog {
    // 默认打印栈深度
    public static int DEFAULT_STACK = 0;
    // log开关
    public static boolean debug = BuildConfig.DEBUG;
    public static boolean saveLocal = false;
    private static String tag = "CHEN";

    public static boolean isDebug() {
        return debug;
    }

    public static void setDebug(boolean debug) {
        MyLog.debug = debug;
    }

    public static boolean writeToFile() {
        return saveLocal;
    }

    public static void i(String msg) {
        i(null, msg, DEFAULT_STACK);
    }

    public static void i(String tag, String msg) {
        i(tag, msg, DEFAULT_STACK);
    }

    public static void info(String tag, String msg) {
        i(tag, msg, Integer.MAX_VALUE);
    }

    public static void d(String msg) {
        d(null, msg, DEFAULT_STACK);
    }

    public static void d(String tag, String msg) {
        d(tag, msg, DEFAULT_STACK);
    }

    public static void debug(String tag, String msg) {
        d(tag, msg, Integer.MAX_VALUE);
    }

    public static void w(String msg) {
        w(null, msg, DEFAULT_STACK);
    }

    public static void w(String tag, String msg) {
        w(tag, msg, DEFAULT_STACK);
    }

    public static void warn(String tag, String msg) {
        w(tag, msg, Integer.MAX_VALUE);
    }

    public static void e(String msg) {
        e(tag, msg, DEFAULT_STACK);
    }

    public static void e(String tag, String msg) {
        e(tag, msg, DEFAULT_STACK);
    }

    public static void e(Throwable throwable) {
        e(null, Log.getStackTraceString(throwable), 0);
    }

    public static void error(String tag, String msg) {
        e(tag, msg, Integer.MAX_VALUE);
    }

    public static void i(String tag, String msg, int stackCount) {
        if (!debug) return;
        boolean logMsg = false;
        StringBuilder logBuilder = new StringBuilder();
        // 获取当前线程的堆栈
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        String startSplit = "----------------------------------start----------------------------------";
        String EndSplit = "-----------------------------------end-----------------------------------";
        int count = stackCount;
        for (int i = 0; i < stackTrace.length; i++) {
            StackTraceElement s = stackTrace[i];
            if (s.getMethodName().equals("getThreadStackTrace") || s.getMethodName().equals("getStackTrace") || s.getMethodName().equals("i")) {
                continue;
            }

            if (tag == null) {
                String fileName = s.getFileName();
                if (fileName != null) {
                    tag = fileName.replace(".java", "").replace(".kt", "");
                } else {
                    tag = MyLog.tag;
                }
            }
            if (stackCount != 0 && !logMsg) {
                Log.i(tag, startSplit);
                logBuilder.append(tag).append(" ").append(startSplit).append("\n");
            }
            if (!logMsg) {
                Log.i(tag, msg);
                logBuilder.append(tag).append(" ").append(msg);
                if (stackCount != 0) {
                    Log.i(tag, "from method:");
                    logBuilder.append(tag).append(" ").append("from method:").append("\n");
                }
                logMsg = true;
            }
            if (count > 0) {
                Log.i(tag, s.toString());
                logBuilder.append(tag).append(" ").append(s.toString()).append("\n");
                count--;
            }
            if (stackCount != 0 && i == stackTrace.length - 1) {
                Log.i(tag, EndSplit);
                logBuilder.append(tag).append(" ").append(EndSplit).append("\n");
            }
        }
        // 写到本地文件
        if (writeToFile()) {

        }
    }

    public synchronized static void d(String tag, String msg, int stackCount) {
        if (!debug) return;
        if (!TextUtils.isEmpty(tag) && !TextUtils.isEmpty(msg) && stackCount == 0) {
            Log.d(tag, msg);
            return;
        }
        boolean logMsg = false;
        StringBuilder logBuilder = new StringBuilder();
        // 获取当前线程的堆栈
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        String startSplit = "----------------------------------start----------------------------------";
        String EndSplit = "-----------------------------------end-----------------------------------";
        int count = stackCount;
        for (int i = 0; i < stackTrace.length; i++) {
            StackTraceElement s = stackTrace[i];
            if (s.getMethodName().equals("getThreadStackTrace") || s.getMethodName().equals("getStackTrace") || s.getMethodName().equals("d")) {
                continue;
            }

            if (tag == null) {
                String fileName = s.getFileName();
                if (fileName != null) {
                    tag = fileName.replace(".java", "").replace(".kt", "");
                } else {
                    tag = MyLog.tag;
                }
            }
            if (stackCount != 0 && !logMsg) {
                Log.d(tag, startSplit);
                logBuilder.append(tag).append(" ").append(startSplit).append("\n");
            }
            if (!logMsg) {
                Log.d(tag, msg);
                logBuilder.append(tag).append(" ").append(msg);
                if (stackCount != 0) {
                    Log.d(tag, "from method:");
                    logBuilder.append(tag).append(" ").append("from method:").append("\n");
                }
                logMsg = true;
            }
            if (count > 0) {
                Log.d(tag, s.toString());
                logBuilder.append(tag).append(" ").append(s.toString()).append("\n");
                count--;
            }
            if (stackCount != 0 && i == stackTrace.length - 1) {
                Log.d(tag, EndSplit);
                logBuilder.append(tag).append(" ").append(EndSplit).append("\n");
            }
        }
        // 写到本地文件
        if (writeToFile()) {

        }
    }

    public static void w(String tag, String msg, int stackCount) {
        if (!debug) return;
        boolean logMsg = false;
        StringBuilder logBuilder = new StringBuilder();
        // 获取当前线程的堆栈
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        String startSplit = "----------------------------------start----------------------------------";
        String EndSplit = "-----------------------------------end-----------------------------------";
        int count = stackCount;
        for (int i = 0; i < stackTrace.length; i++) {
            StackTraceElement s = stackTrace[i];
            if (s.getMethodName().equals("getThreadStackTrace") || s.getMethodName().equals("getStackTrace") || s.getMethodName().equals("w")) {
                continue;
            }

            if (tag == null) {
                String fileName = s.getFileName();
                if (fileName != null) {
                    tag = fileName.replace(".java", "").replace(".kt", "");
                } else {
                    tag = MyLog.tag;
                }
            }
            if (stackCount != 0 && !logMsg) {
                Log.w(tag, startSplit);
                logBuilder.append(tag).append(" ").append(startSplit).append("\n");
            }
            if (!logMsg) {
                Log.w(tag, msg);
                logBuilder.append(tag).append(" ").append(msg);
                if (stackCount != 0) {
                    Log.w(tag, "from method:");
                    logBuilder.append(tag).append(" ").append("from method:").append("\n");
                }
                logMsg = true;
            }
            if (count > 0) {
                Log.w(tag, s.toString());
                logBuilder.append(tag).append(" ").append(s.toString()).append("\n");
                count--;
            }
            if (stackCount != 0 && i == stackTrace.length - 1) {
                Log.w(tag, EndSplit);
                logBuilder.append(tag).append(" ").append(EndSplit).append("\n");
            }
        }
        // 写到本地文件
        if (writeToFile()) {

        }
    }

    public static void e(String tag, String msg, int stackCount) {
        if (!debug) return;
        boolean logMsg = false;
        StringBuilder logBuilder = new StringBuilder();
        // 获取当前线程的堆栈
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        String startSplit = "----------------------------------start----------------------------------";
        String EndSplit =   "-----------------------------------end-----------------------------------";
        int count = stackCount;
        for (int i = 0; i < stackTrace.length; i++) {
            StackTraceElement s = stackTrace[i];
            if (s.getMethodName().equals("getThreadStackTrace") || s.getMethodName().equals("getStackTrace") || s.getMethodName().equals("e")) {
                continue;
            }

            if (tag == null) {
                String fileName = s.getFileName();
                if (fileName != null) {
                    tag = fileName.replace(".java", "").replace(".kt", "");
                } else {
                    tag = MyLog.tag;
                }
            }
            if (stackCount != 0 && !logMsg) {
                Log.e(tag, startSplit);
                logBuilder.append(tag).append(" ").append(startSplit).append("\n");
            }
            if (!logMsg) {
                Log.e(tag, msg);
                logBuilder.append(tag).append(" ").append(msg);
                if (stackCount != 0) {
                    Log.e(tag, "from method:");
                    logBuilder.append(tag).append(" ").append("from method:").append("\n");
                }
                logMsg = true;
            }
            if (count > 0) {
                Log.e(tag, s.toString());
                logBuilder.append(tag).append(" ").append(s.toString()).append("\n");
                count--;
            }
            if (stackCount != 0 && i == stackTrace.length - 1) {
                Log.e(tag, EndSplit);
                logBuilder.append(tag).append(" ").append(EndSplit).append("\n");
            }
        }
        // 写到本地文件
        if (writeToFile()) {

        }
    }
}
