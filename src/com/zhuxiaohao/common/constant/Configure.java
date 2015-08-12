package com.zhuxiaohao.common.constant;

import android.app.Activity;
import android.util.DisplayMetrics;

/**
 * ClassName: Configure <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON(可选). <br/>
 * date: 2014年11月20日 下午5:52:24 <br/>
 * 取屏幕宽高
 * 
 * @author chenhao
 * @version
 * @since JDK 1.6
 */
public class Configure {
	/** 是否移动 */
	public static boolean isMove = false;
	/** 是否换页 */
	public static boolean isChangingPage = false;
	/** 删除按钮变暗 */
	public static boolean isDelDark = false;
	/** 屏幕高度 */
	public static int screenHeight = 0;
	/** 屏幕宽度 */
	public static int screenWidth = 0;
	/** 屏幕密度 */
	public static float screenDensity = 0;
	/** 当前页 */
	public static int curentPage = 0;
	/** 总的页数 */
	public static int countPages = 0;
	/** 删除的页 */
	public static int removeItem = 0;
	/** 是否显示删除按钮 */
	public static boolean isdeleteShow = false;
	/** 交换item */
	public static boolean isChangeItem = false;

	/**
	 * 
	 * init:(初始化获取宽高). <br/>
	 * @author chenhao
	 * @param context
	 * @since JDK 1.6
	 */
	public static void init(Activity context) {
		if (screenDensity == 0 || screenWidth == 0 || screenHeight == 0) {
			DisplayMetrics dm = new DisplayMetrics();
			context.getWindowManager().getDefaultDisplay().getMetrics(dm);
			Configure.screenDensity = dm.density;
			Configure.screenHeight = dm.heightPixels;
			Configure.screenWidth = dm.widthPixels;
		}
		curentPage = 0;
		countPages = 0;
	}

	public int[] ret(int[] intArray) {
		int size = intArray.length;
		for (int i = size - 1; i >= 0; i--)
			for (int j = 0; j < i; j++)
				if (intArray[j] > intArray[j + 1]) {
					int t = intArray[j];
					intArray[j] = intArray[j + 1];
					intArray[j + 1] = t;
				}
		return intArray;
	}

	/**
	 * 
	 * getScreenHeight:(获取高度). <br/>
	 * 
	 * @author chenhao
	 * @param context
	 * @return
	 * @since JDK 1.6
	 */
	public static int getScreenHeight(Activity context) {
		if (screenWidth == 0 || screenHeight == 0) {
			DisplayMetrics dm = new DisplayMetrics();
			context.getWindowManager().getDefaultDisplay().getMetrics(dm);
			Configure.screenDensity = dm.density;
			Configure.screenHeight = dm.heightPixels;
			Configure.screenWidth = dm.widthPixels;
		}
		return screenHeight;
	}

	/**
	 * 
	 * getScreenWidth:(获取宽度). <br/>
	 * 
	 * @author chenhao
	 * @param context
	 * @return
	 * @since JDK 1.6
	 */
	public static int getScreenWidth(Activity context) {
		if (screenWidth == 0 || screenHeight == 0) {
			DisplayMetrics dm = new DisplayMetrics();
			context.getWindowManager().getDefaultDisplay().getMetrics(dm);
			Configure.screenDensity = dm.density;
			Configure.screenHeight = dm.heightPixels;
			Configure.screenWidth = dm.widthPixels;
		}
		return screenWidth;
	}
}
