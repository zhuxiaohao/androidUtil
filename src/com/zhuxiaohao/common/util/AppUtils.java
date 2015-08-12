package com.zhuxiaohao.common.util;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

/**
 * 
 * ClassName: AppUtils <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON(可选). <br/>
 * date: 2014年11月20日 下午6:34:28 <br/>
 * 应用工具
 * 
 * @author chenhao
 * @version
 * @since JDK 1.6
 */
public class AppUtils {

	private AppUtils() {
		throw new AssertionError();
	}

	/**
	 * 这一过程是否与processName命名
	 * 
	 * @param context
	 * @param processName
	 * @return <ul>
	 *         return whether this process is named with processName
	 *         <li>if context is null, return false</li>
	 *         <li>if {@link ActivityManager#getRunningAppProcesses()} is null,
	 *         return false</li>
	 *         <li>if one process of
	 *         {@link ActivityManager#getRunningAppProcesses()} is equal to
	 *         processName, return true, otherwise return false</li>
	 *         </ul>
	 */
	public static boolean isNamedProcess(Context context, String processName) {
		if (context == null) {
			return false;
		}

		int pid = android.os.Process.myPid();
		ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> processInfoList = manager.getRunningAppProcesses();
		if (processInfoList == null) {
			return true;
		}

		for (RunningAppProcessInfo processInfo : manager.getRunningAppProcesses()) {
			if (processInfo.pid == pid && ObjectUtils.isEquals(processName, processInfo.processName)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 应用程序是否在界面
	 * <ul>
	 * <li>need use permission android.permission.GET_TASKS in Manifest.xml</li>
	 * </ul>
	 * 
	 * @param context
	 * @return 如果应用程序在后台返回true，否则返回假
	 */
	public static boolean isApplicationInBackground(Context context) {
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> taskList = am.getRunningTasks(1);
		if (taskList != null && !taskList.isEmpty()) {
			ComponentName topActivity = taskList.get(0).topActivity;
			if (topActivity != null && !topActivity.getPackageName().equals(context.getPackageName())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 
	 * getAppName:(获取应用程序名称). <br/>
	 * 
	 * @author chenhao
	 * @param context
	 * @return
	 * @since JDK 1.6
	 */
	public static String getAppName(Context context) {
		try {
			PackageManager packageManager = context.getPackageManager();
			PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
			int labelRes = packageInfo.applicationInfo.labelRes;
			return context.getResources().getString(labelRes);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * getVersionName:(获取应用程序版本名称信息). <br/>
	 * 
	 * @author chenhao
	 * @param context
	 * @return 当前应用的版本名称
	 * @since JDK 1.6
	 */
	public static String getVersionName(Context context) {
		try {
			PackageManager packageManager = context.getPackageManager();
			PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionName;

		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
}
