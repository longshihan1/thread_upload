package com.dodola.thread;

import android.app.Application;
import android.text.TextUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;

public final class ThreadHook {
    static {
        System.loadLibrary("threadhook");
    }
    private String s;

    public static Application application;
    private static boolean sHasHook = false;
    private static boolean sHookFailed = false;
    public static String filename;


    public static String getStack() {
        String message= stackTraceToString(new Throwable().getStackTrace());
        if (TextUtils.isEmpty(filename)){
            return message;
        }
        String strDateFormat = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat);
        message="----------------"+sdf.format(System.currentTimeMillis())+"----------------\n"+message;
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(new File(filename),true);
            FileChannel channel = fos.getChannel();
            ByteBuffer src = Charset.forName("utf8").encode(message);
            int length = 0;
            while ((length = channel.write(src)) != 0) {
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
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
        try {
            //开启一个文件读写
            File file=application1.getExternalCacheDir();
            if (file==null){
                return;
            }
            if(!file.exists()){
                file.mkdirs();
            }
            String strDateFormat = "yyyy-MM-dd HH:mm:ss";
            SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat);
            filename=file.getAbsolutePath()+"/thread-"+sdf.format(System.currentTimeMillis())+".txt";
            File file1=new File(filename);
            if (!file1.exists()){
                file1.createNewFile();
            }
        }catch (Exception e){
            e.getMessage();
        }

    }




    private static native void enableThreadHookNative();


}