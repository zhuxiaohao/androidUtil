package com.zhuxiaohao.common.service;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.zhuxiaohao.common.entity.CacheObject;

/**
 * 
 * ClassName: Cache <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON(可选). <br/>
 * date: 2014年11月20日 下午6:32:27 <br/>
 * 高速缓存接口
 * @author chenhao
 * @version @param <K>
 * @version @param <V>
 * @since JDK 1.6
 */
public interface Cache<K, V> {

    /**
     * get object in cache
     * 
     * @return
     */
    public int getSize();

    /**
     * get object
     * 
     * @param key
     * @return
     */
    public CacheObject<V> get(K key);

    /**
     * put object
     * 
     * @param key
     *            key
     * @param value
     *            data in object, {@link CacheObject#getData()}
     * @return
     */
    public CacheObject<V> put(K key, V value);

    /**
     * put object
     * 
     * @param key
     *            key
     * @param value
     *            object
     * @return
     */
    public CacheObject<V> put(K key, CacheObject<V> value);

    /**
     * put all object in cache2
     * 
     * @param cache2
     */
    public void putAll(Cache<K, V> cache2);

    /**
     * whether key is in cache
     * 
     * @param key
     * @return
     */
    public boolean containsKey(K key);

    /**
     * remove object
     * 
     * @param key
     * @return the object be removed
     */
    public CacheObject<V> remove(K key);

    /**
     * clear cache
     */
    public void clear();

    /**
     * get hit rate
     * 
     * @return
     */
    public double getHitRate();

    /**
     * key set
     * 
     * @return
     */
    public Set<K> keySet();

    /**
     * key value set
     * 
     * @return
     */
    public Set<Map.Entry<K, CacheObject<V>>> entrySet();

    /**
     * value set
     * 
     * @return
     */
    public Collection<CacheObject<V>> values();
}
