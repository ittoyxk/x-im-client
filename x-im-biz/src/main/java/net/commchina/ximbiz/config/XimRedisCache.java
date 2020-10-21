package net.commchina.ximbiz.config;

import cn.hutool.core.convert.Convert;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import net.commchina.framework.boot.redis.EnhancedRedisService;
import net.commchina.framework.common.util.SpringContextUtil;
import org.apache.commons.lang3.StringUtils;
import org.jim.core.cache.ICache;
import org.jim.core.utils.JsonKit;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @description: x-im
 * @author: hengxiaokang
 * @time 2020/10/20 14:37
 */
@Slf4j
public class XimRedisCache implements ICache {

    private EnhancedRedisService enhancedRedisService;

    private RedisTemplate redisTemplate;

    public static String cacheKey(String cacheName, String key)
    {
        return keyPrefix(cacheName) + key;
    }

    public static String keyPrefix(String cacheName)
    {
        return cacheName + ":";
    }

    private String cacheName = null;

    private Integer timeToLiveSeconds = null;

    private Integer timeToIdleSeconds = null;

    private Integer timeout = null;

    public XimRedisCache(String cacheName, Integer timeToLiveSeconds, Integer timeToIdleSeconds)
    {
        this.cacheName = cacheName;
        this.timeToLiveSeconds = timeToLiveSeconds;
        this.timeToIdleSeconds = timeToIdleSeconds;
        this.timeout = this.timeToLiveSeconds == null ? this.timeToIdleSeconds : this.timeToLiveSeconds;
        this.enhancedRedisService = SpringContextUtil.getBean(EnhancedRedisService.class);
        this.redisTemplate=SpringContextUtil.getBean("redis");
    }

    @Override
    public void clear()
    {
        try {
            Set<String> keys = enhancedRedisService.keys(keyPrefix(cacheName) + "*");
            enhancedRedisService.del(keys.stream().collect(Collectors.toList()));
        } catch (Exception e) {
            log.error(e.toString(), e);
        }
    }

    @Override
    public Serializable get(String key)
    {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        Serializable value = null;
        try {
            value =enhancedRedisService.get(cacheKey(cacheName, key), Serializable.class);
            if (timeToIdleSeconds != null) {
                if (value != null) {
                    enhancedRedisService.expire(key,timeout, TimeUnit.SECONDS);
                }
            }
        } catch (Exception e) {
            log.error(e.toString(), e);
        }
        return value;
    }

    @Override
    public <T> T get(String key, Class<T> clazz)
    {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        T value = null;
        try {
            value = enhancedRedisService.get(cacheKey(cacheName, key), clazz);
            if (timeToIdleSeconds != null) {
                if (value != null) {
                    enhancedRedisService.expire(key,timeout, TimeUnit.SECONDS);
                }
            }
        } catch (Exception e) {
            log.error(e.toString(), e);
        }
        return value;
    }

    @Override
    public Collection<String> keys()
    {
        try {
            return enhancedRedisService.keys(keyPrefix(cacheName));
        } catch (Exception e) {
            log.error(e.toString(), e);
        }
        return null;
    }

    @Override
    public void put(String key, Serializable value)
    {
        if (StringUtils.isBlank(key)) {
            return;
        }
        try {
            enhancedRedisService.set(cacheKey(cacheName, key), value instanceof String ? value.toString(): JSON.toJSONString(value), Integer.parseInt(timeout + ""),TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error(e.toString(), e);
        }
    }

    public void listPushTail(String key, Serializable value)
    {
        if (StringUtils.isBlank(key)) {
            return;
        }
        try {
            String jsonValue = value instanceof String ? (String) value : JsonKit.toJSONString(value);
            enhancedRedisService.rPush(cacheKey(cacheName, key), jsonValue);
        } catch (Exception e) {
            log.error(e.toString(), e);
        }
    }

    public List<String> listGetAll(String key)
    {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        try {
            return enhancedRedisService.lRange(cacheKey(cacheName, key),0,-1);
        } catch (Exception e) {
            log.error(e.toString(), e);
        }
        return null;
    }

    public Long listRemove(String key, String value)
    {
        if (StringUtils.isBlank(key) || StringUtils.isBlank(value)) {
            return 0L;
        }
        try {
            return enhancedRedisService.lRem(cacheKey(cacheName, key),  value);
        } catch (Exception e) {
            log.error(e.toString(), e);
        }
        return 0L;
    }

    public void sortSetPush(String key, double score, Serializable value)
    {
        if (StringUtils.isBlank(key)) {
            return;
        }
        try {
            String jsonValue = value instanceof String ? (String) value : JsonKit.toJSONString(value);
            String k=cacheKey(cacheName, key);
           enhancedRedisService.zAdd(k,  jsonValue,score);
        } catch (Exception e) {
            log.error(e.toString(), e);
        }
    }

    public List<String> sortSetGetAll(String key)
    {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        try {
            List<String> dataSet = enhancedRedisService.zRangeRemByScore(cacheKey(cacheName, key), Long.MIN_VALUE, Long.MAX_VALUE);
            if (dataSet == null) {
                return null;
            }
            return dataSet;
        } catch (Exception e) {
            log.error(e.toString(), e);
        }
        return null;
    }

    public List<String> sortSetGetAll(String key, double min, double max)
    {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        try {
            List<String> dataSet = enhancedRedisService.zRangeRemByScore(cacheKey(cacheName, key), Convert.toLong(min), Convert.toLong(max));
            if (dataSet == null) {
                return null;
            }
            return dataSet;
        } catch (Exception e) {
            log.error(e.toString(), e);
        }
        return null;
    }

    public List<String> sortSetGetAll(String key, double min, double max, int offset, int count)
    {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        try {
            List<String> dataSet = enhancedRedisService
                    .zRangeRemByScoreAndLimit(cacheKey(cacheName, key), Convert.toLong(min), Convert.toLong(max), offset, count);
            if (dataSet == null) {
                return null;
            }
            return dataSet;
        } catch (Exception e) {
            log.error(e.toString(), e);
        }
        return null;
    }

    @Override
    public void putTemporary(String key, Serializable value)
    {
        if (StringUtils.isBlank(key)) {
            return;
        }
        try {
            enhancedRedisService.set(cacheKey(cacheName, key), value.toString(), 10,TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error(e.toString(), e);
        }
    }

    @Override
    public void remove(String key)
    {
        if (StringUtils.isBlank(key)) {
            return;
        }
        try {
            enhancedRedisService.del(cacheKey(cacheName, key));
        } catch (Exception e) {
            log.error(e.toString(), e);
        }
    }

    public String getCacheName()
    {
        return cacheName;
    }

    public Integer getTimeout()
    {
        return timeout;
    }

    public Integer getTimeToIdleSeconds()
    {
        return timeToIdleSeconds;
    }

    public Integer getTimeToLiveSeconds()
    {
        return timeToLiveSeconds;
    }
}
