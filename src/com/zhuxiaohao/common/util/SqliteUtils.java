package com.zhuxiaohao.common.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * 
 * ClassName: SqliteUtils <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON(可选). <br/>
 * date: 2014年11月20日 下午6:48:04 <br/>
 * SQLite的工具
 * @author chenhao
 * @version 
 * @since JDK 1.6
 */
public class SqliteUtils {

    private static volatile SqliteUtils instance;

    private DbHelper dbHelper;
    private SQLiteDatabase db;

    private SqliteUtils(Context context) {
        dbHelper = new DbHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    public static SqliteUtils getInstance(Context context) {
        if (instance == null) {
            synchronized (SqliteUtils.class) {
                if (instance == null) {
                    instance = new SqliteUtils(context);
                }
            }
        }
        return instance;
    }

    public SQLiteDatabase getDb() {
        return db;
    }
}
