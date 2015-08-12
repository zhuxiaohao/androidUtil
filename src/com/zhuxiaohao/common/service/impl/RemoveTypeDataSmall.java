package com.zhuxiaohao.common.service.impl;

import com.zhuxiaohao.common.entity.CacheObject;
import com.zhuxiaohao.common.service.CacheFullRemoveType;
import com.zhuxiaohao.common.util.ObjectUtils;

/**
 * 
 * ClassName: RemoveTypeDataSmall <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON(可选). <br/>
 * date: 2014年11月20日 下午6:24:26 <br/>
 * Remove type when cache is full.when cache is full, compare data of object in cache, if data is smaller remove it first.
 * @author chenhao
 * @version @param <T>
 * @since JDK 1.6
 */
public class RemoveTypeDataSmall<T> implements CacheFullRemoveType<T> {

    private static final long serialVersionUID = 1L;

    @Override
    public int compare(CacheObject<T> obj1, CacheObject<T> obj2) {
        return ObjectUtils.compare(obj1.getData(), obj2.getData());
    }
}
