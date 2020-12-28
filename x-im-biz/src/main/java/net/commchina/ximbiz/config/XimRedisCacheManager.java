package net.commchina.ximbiz.config;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @description: x-im
 * @author: hengxiaokang
 * @time 2020/10/20 14:36
 */
@Slf4j
public class XimRedisCacheManager {
    private static Map<String, XimRedisCache> map = new ConcurrentHashMap<>();

    public static XimRedisCache getCache(String cacheName) {
        XimRedisCache redisCache = map.get(cacheName);
        if (redisCache == null) {
            log.error("cacheName[{}]还没注册，请初始化时调用：{}.register(redisson, cacheName, timeToLiveSeconds, timeToIdleSeconds)", cacheName, XimRedisCache.class.getSimpleName());
        }
        return redisCache;
    }

    /**
     * timeToLiveSeconds和timeToIdleSeconds不允许同时为null
     * @param cacheName
     * @param timeToLiveSeconds
     * @param timeToIdleSeconds
     * @return
     * @author wchao
     */
    public static XimRedisCache register(String cacheName, Integer timeToLiveSeconds, Integer timeToIdleSeconds) {
        XimRedisCache redisCache = map.get(cacheName);
        if (redisCache == null) {
            synchronized (XimRedisCacheManager.class) {
                redisCache = map.get(cacheName);
                if (redisCache == null) {
                    redisCache = new XimRedisCache(cacheName, timeToLiveSeconds, timeToIdleSeconds);
                    map.put(cacheName, redisCache);
                }
            }
        }
        return redisCache;
    }
}
