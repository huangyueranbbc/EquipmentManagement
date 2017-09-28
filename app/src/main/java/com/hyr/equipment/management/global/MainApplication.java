package com.hyr.equipment.management.global;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Process;

import com.uuzuche.lib_zxing.activity.ZXingLibrary;

/**
 * Created by huangyueran on 2017/1/13.
 * 自定义Appliaction 进行全局初始化
 */
public class MainApplication extends Application {

    // 定义全局公用变量
    private static Context context;
    private static Handler handler;
    private static int mainThreadId;
    private static int myPid;

    @Override
    public void onCreate() {
        super.onCreate();

        //  初始化ZXingLibrary
        ZXingLibrary.initDisplayOpinion(this);

        context = getApplicationContext();
        handler = new Handler();
        //进程ID
        myPid = Process.myPid();
        //线程ID 此处是主线程id
        mainThreadId = Process.myTid();

    }


    public static Context getContext() {
        return context;
    }

    public static Handler getHandler() {
        return handler;
    }

    public static int getMainThreadId() {
        return mainThreadId;
    }

    public static int getMyPid() {
        return myPid;
    }

}