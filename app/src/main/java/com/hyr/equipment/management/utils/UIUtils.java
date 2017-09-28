package com.hyr.equipment.management.utils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Process;
import android.view.View;

import com.hyr.equipment.management.global.MainApplication;

/**
 * Created by huangyueran on 2017/1/13.
 * UI工具类
 */
public class UIUtils {

    /**
     * 获取Context
     *
     * @return
     */
    public static Context getContext() {
        return MainApplication.getContext();
    }

    /**
     * 获取Handler
     *
     * @return
     */
    public static Handler getHandler() {
        return MainApplication.getHandler();
    }

    /**
     * 获取主线程ID
     *
     * @return
     */
    public static int getMainThreadId() {
        return MainApplication.getMainThreadId();
    }

    /**
     * 获取进程ID
     *
     * @return
     */
    public static int getPid() {
        return MainApplication.getMyPid();
    }

    /**
     * 获取String
     *
     * @return
     */
    public static String getString(int id) {
        return getContext().getResources().getString(id);
    }

    /**
     * 获取字符串数组
     *
     * @return
     */
    public static String[] getStringArray(int id) {
        return getContext().getResources().getStringArray(id);
    }

    /**
     * 获取图片
     */
    public static Drawable getDrawable(int id) {
        return getContext().getResources().getDrawable(id);
    }

    /**
     * 获取颜色
     *
     * @param id
     * @return
     */
    public static int getColor(int id) {
        return getContext().getResources().getColor(id);
    }


    /**
     * 获取尺寸
     *
     * @param id
     * @return
     */
    public static int getDimen(int id) {
        return getContext().getResources().getDimensionPixelSize(id); //直接返回像素值
    }

    /**
     * TODO
     * 根据id获取颜色的状态选择器
     *
     * @param mTabTextColorResId
     * @return
     */
    public static ColorStateList getColorStateList(int mTabTextColorResId) {
        return getContext().getResources().getColorStateList(mTabTextColorResId);
    }

    //========================= dip和px转换 ===============================

    /**
     * dip转px
     *
     * @param dip
     * @return
     */
    public static int dip2px(float dip) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return (int) (dip * density + 05f);
    }

    /**
     * px转dip
     *
     * @param px
     * @return
     */
    public static float px2dip(float px) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return px / density;
    }

    //========================= 加载布局文件 ===============================

    /**
     * 加载布局文件View
     *
     * @param id
     * @return
     */
    public static View inflate(int id) {
        return View.inflate(getContext(), id, null);
    }

    //========================= 判断是否运行在主线程 ===============================

    /**
     * 是否允许在主线程
     *
     * @return
     */
    public static boolean isRunOnUIThread() {
        //获取当前线程id，如果当前线程id和主线程id相同,返回true,在主线程运行。
        int myTid = Process.myTid();
        if (myTid == getMainThreadId()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 在主线程运行
     *
     * @param r
     */
    public static void runOnUIThread(Runnable r) {
        if (isRunOnUIThread()) {
            // 如果已经是主线程，直接运行
            r.run();
        } else {
            // 如果是子线程，借助Handler让其运行在主线程
            getHandler().post(r);
        }
    }

}
