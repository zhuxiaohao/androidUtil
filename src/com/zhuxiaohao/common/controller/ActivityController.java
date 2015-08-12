/**
 * Project Name:androidTools
 * File Name:ActivityController.java
 * Package Name:com.zhuxiaohao.common.activitycontroller
 * Date:2015-4-23下午9:39:02
 * Copyright (c) 2015, zhuxiaohao All Rights Reserved.
 *
 */

package com.zhuxiaohao.common.controller;
import java.util.ArrayList;
import java.util.List;
import android.app.Activity;

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
}
