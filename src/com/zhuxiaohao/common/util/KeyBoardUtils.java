/**
 * Project Name:android-common-tools
 * File Name:KeyBoardUtils.java
 * Package Name:com.android.common.util
 * Date:2015年3月2日上午10:37:10
 * Copyright (c) 2015, zhuxiaohao All Rights Reserved.
 *
 */

package com.zhuxiaohao.common.util;

import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * ClassName:KeyBoardUtils <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2015年3月2日 上午10:37:10 <br/>
 * 
 * @author chenhao
 * @version
 * @since JDK 1.6
 * @see 打开或关闭软键盘
 */
public class KeyBoardUtils {
    /**
     * 打开软键盘
     * 
     * @param mEditText
     *            输入框
     * @param mContext
     *            上下文
     */
    public static void openKeybord(EditText mEditText, Context mContext) {
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(mEditText, InputMethodManager.RESULT_SHOWN);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    /**
     * 关闭软键盘
     * 
     * @param mEditText
     *            输入框
     * @param mContext
     *            上下文
     */
    public static void closeKeybord(EditText mEditText, Context mContext) {
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
    }
}
