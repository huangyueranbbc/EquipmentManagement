package com.hyr.equipment.management.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * 登录工具类
 */
public class LoginUtils {

    /**
     * @param ctx
     * @param target      目标activity的action
     * @param params      需要传给目标activity的参数
     * @param intent 带参数的指向登录页的intent
     */
    public static void jumpToActivity(Context ctx, String target, Bundle params,
                                      Intent intent) {
        // TODO 验证是否登录

        // 如果登录 才进行Activity的跳转

//        if (TextUtils.isEmpty(target) || loginIntent == null)
//            throw new RuntimeException("No target activity.");
//
//        JumpInvoker invoker = new JumpInvoker(target, params);
//        if (logon()) {
//            invoker.invoke(ctx);
//        } else {
//            loginIntent.putExtra(INVOKER, invoker);
//            loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            ctx.startActivity(loginIntent);
//        }
    }

}
