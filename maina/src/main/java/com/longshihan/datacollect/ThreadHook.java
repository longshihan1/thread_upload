package com.longshihan.datacollect;

import android.app.Application;

public final class ThreadHook {
    static {
        System.loadLibrary("threadhook");
    }
    private String s;

    public static Application application;
    public static boolean sHasHook = false;
    public static boolean sHookFailed = false;
    private static OnThreadCreateTimeListener onThreadCreateTimeListener;

    public static void registerThreadListener(OnThreadCreateTimeListener onThreadCreateTimeListener){
        ThreadHook.onThreadCreateTimeListener=onThreadCreateTimeListener;
    }

    public static String getStack() {
        String message= stackTraceToString(new Throwable().getStackTrace());
        if (ThreadHook.onThreadCreateTimeListener!=null&&ThreadHook.onThreadCreateTimeListener.onThreadCreate(message)){
            return "";
        }
        return message;
    }

    private static String stackTraceToString(final StackTraceElement[] arr) {
        if (arr == null) {
            return "";
        }

        StringBuffer sb = new StringBuffer();

        for (StackTraceElement stackTraceElement : arr) {
            String className = stackTraceElement.getClassName();
            // remove unused stacks
            if (className.contains("java.lang.Thread")) {
                continue;
            }
            sb.append(stackTraceElement).append('\n');
        }
        return sb.toString();
    }

    public static void enableThreadHook(Application application1) {
        application=application1;
        if (sHasHook) {
            return;
        }
        sHasHook = true;
        enableThreadHookNative();
    }


    public interface OnThreadCreateTimeListener{
        boolean onThreadCreate(String msg);
    }


    private static native void enableThreadHookNative();


}