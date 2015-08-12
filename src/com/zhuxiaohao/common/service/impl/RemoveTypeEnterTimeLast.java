package com.zhuxiaohao.common.service.impl;

import com.zhuxiaohao.common.entity.CacheObject;
import com.zhuxiaohao.common.service.CacheFullRemoveType;

/**
 * 
 * ClassName: RemoveTypeEnterTimeLast <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON(可选). <br/>
 * date: 2014年11月20日 下午6:25:52 <br/>
 *拆卸式缓存时已满。当高速缓存已满，进入时间的高速缓存中的对象进行比较，如果时间是较小的 首先删除。同时后进先出
 * @author chenhao
 * @version @param <T>
 * @since JDK 1.6
 */
public class RemoveTypeEnterTimeLast<T> implements CacheFullRemoveType<T> {

    private static final long serialVersionUID = 1L;

    @Override
    public int compare(CacheObject<T> obj1, CacheObject<T> obj2) {
        return (obj2.getEnterTime() > obj1.getEnterTime()) ? 1 : ((obj2.getEnterTime() == obj1.getEnterTime()) ? 0 : -1);
    }
}
