package com.zhuxiaohao.common.service.impl;

import com.zhuxiaohao.common.entity.CacheObject;
import com.zhuxiaohao.common.service.CacheFullRemoveType;
import com.zhuxiaohao.common.util.ImageUtils;

import android.graphics.Bitmap;

/**
 * 
 * ClassName: RemoveTypeBitmapSmall <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON(可选). <br/>
 * date: 2014年11月20日 下午6:23:17 <br/>
 * 删除类型时，高速缓存已满，缓存的数据类型是位图
 * @author chenhao
 * @version 
 * @since JDK 1.6
 */
public class RemoveTypeBitmapSmall implements CacheFullRemoveType<Bitmap> {

    private static final long serialVersionUID = 1L;

    @Override
    public int compare(CacheObject<Bitmap> obj1, CacheObject<Bitmap> obj2) {
        long sizeOfFile1 = getSize(obj1);
        long sizeOfFile2 = getSize(obj2);
        if (sizeOfFile1 == sizeOfFile2) {
            if (obj1.getUsedCount() == obj2.getUsedCount()) {
                return (obj1.getEnterTime() > obj2.getEnterTime()) ? 1 : ((obj1.getEnterTime() == obj2.getEnterTime()) ? 0 : -1);
            }
            return (obj1.getUsedCount() > obj2.getUsedCount() ? 1 : -1);
        }
        return (sizeOfFile1 > sizeOfFile2 ? 1 : -1);
    }

    /**
     * get size of bitmap
     * 
     * @param o
     * @return
     */
    private long getSize(CacheObject<Bitmap> o) {
        if (o == null) {
            return -1;
        }

        // TODO is there any more efficient way?
        byte[] b = ImageUtils.bitmapToByte(o.getData());
        return (b == null ? -1 : b.length);
    }
}
