/**
 * Project Name:androidTools
 * File Name:ActivityController.java
 * Package Name:com.zhuxiaohao.common.activitycontroller
 * Date:2015-4-23下午9:39:02
 * Copyright (c) 2015, zhuxiaohao All Rights Reserved.
 */

package com.zhuxiaohao.common.controller;

import com.zhuxiaohao.common.util.Log;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;

/**
 * ClassName:ActivityController <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2015-4-23 下午9:39:02 <br/>
 *
 * @author chenhao
 * @version
 * @since JDK 1.6
 * @see 管理 Activity
 */
public class ActivityController {
    /**
     * 实列
     */
    public static List<Activity> list = new ArrayList<Activity>();

    /**
     *
     * addActivity:(添加 activity). <br/>
     *
     * @author chenhao
     * @param activity
     * @since JDK 1.6
     */
    public static void addActivity(Activity activity) {
        list.add(activity);
    }

    /**
     *
     * removeActivity:(移除 activity). <br/>
     *
     * @author chenhao
     * @param activity
     * @since JDK 1.6
     */
    public static void removeActivity(Activity activity) {
        list.remove(activity);
    }

    /**
     *
     * finshAll:(删除所有活动的 activity). <br/>
     *
     * @author chenhao
     * @since JDK 1.6
     */
    public static void finshAll() {
        for (Activity activity : list) {
            if (!activity.isFinishing()) {
                activity.finish();
            }

        }

    }

    /**
     *  获取当前 app 可用内存
     * @param c
     * @param s
     */
    private static void displayBriefMemory(Context c, String s) {
        final ActivityManager activityManager = (ActivityManager) c.getSystemService(Context.ACTIVITY_SERVICE);
        MemoryInfo info = new MemoryInfo();
        activityManager.getMemoryInfo(info);
        Log.i(s, "系统剩余内存:" + (info.availMem >> 10) + "k");
        Log.i(s, "系统是否处于低内存运行：" + info.lowMemory);
        Log.i(s, "当系统剩余内存低于" + info.threshold + "时就看成低内存运行");
    }
}
