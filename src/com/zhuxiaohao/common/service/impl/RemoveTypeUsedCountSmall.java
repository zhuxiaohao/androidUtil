package com.zhuxiaohao.common.service.impl;

import com.zhuxiaohao.common.entity.CacheObject;
import com.zhuxiaohao.common.service.CacheFullRemoveType;

/**
 * 
 * ClassName: RemoveTypeUsedCountSmall <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON(可选). <br/>
 * date: 2014年11月20日 下午6:31:35 <br/>
 * 拆卸式缓存时已满。当高速缓存已满，比较对象的使用计数缓存，如果是小首先删除。
 * @author chenhao
 * @version @param <T>
 * @since JDK 1.6
 */
public class RemoveTypeUsedCountSmall<T> implements CacheFullRemoveType<T> {

    private static final long serialVersionUID = 1L;

    @Override
    public int compare(CacheObject<T> obj1, CacheObject<T> obj2) {
        return (obj1.getUsedCount() > obj2.getUsedCount()) ? 1 : ((obj1.getUsedCount() == obj2.getUsedCount()) ? 0 : -1);
    }
}
