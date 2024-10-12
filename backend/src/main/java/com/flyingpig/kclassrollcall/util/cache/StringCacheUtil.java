package com.flyingpig.kclassrollcall.util.cache;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class StringCacheUtil {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    public ValueOperations<String, String> getValueOperations() {
        return stringRedisTemplate.opsForValue();
    }

    public StringRedisTemplate getInstance() {
        return stringRedisTemplate;
    }

    public void set(String key, Object value, Long time, TimeUnit unit) {
        stringRedisTemplate.opsForValue().set(key, JSON.toJSONString(value), time, unit);
    }

    public <T> T get(String key, Class<T> type) {
        String value = stringRedisTemplate.opsForValue().get(key);
        if (value != null) {
            return JSON.parseObject(value, type);
        }
        return null; // 允许返回 null
    }

    // 查询时缓存空值防止缓存穿透,互斥锁查询防止缓存击穿
    public <T> T safeGetWithLock(
            String key, Class<T> type, CacheLoader<T> cacheLoader, Long time, TimeUnit unit) {
        String json = stringRedisTemplate.opsForValue().get(key);

        // 命中且不为空字符串，直接返回;命中却为空字符串，返回 null
        if (StrUtil.isNotBlank(json)) {
            return JSON.parseObject(json, type);
        } else if (json != null) {
            return null; // 允许返回 null
        }

        // 获取锁
        String lockKey = String.format("lock:%s", key);
        T result = null;
        RLock rLock = redissonClient.getLock(lockKey);

        try {
            rLock.lock();

            // 再次查询redis，双重判定
            json = stringRedisTemplate.opsForValue().get(key);
            if (StrUtil.isNotBlank(json)) {
                return JSON.parseObject(json, type);
            } else if (json != null) {
                return null; // 允许返回 null
            }

            // 获取锁成功，查询数据库
            result = loadAndSet(key, cacheLoader, time, unit);
        } catch (Exception e) {
            // 记录日志
            log.error("Error occurred while accessing cache with key: {}", key, e);
        } finally {
            if (rLock.isHeldByCurrentThread()) {
                rLock.unlock();
            }
        }

        return result;
    }

    private <T> T loadAndSet(String key, CacheLoader<T> cacheLoader, Long time, TimeUnit unit) {
        T result = cacheLoader.load();

        // 不存在，将空值写入redis，返回 null
        if (result == null) {
            stringRedisTemplate.opsForValue().set(key, "", time, TimeUnit.MINUTES);
        } else {
            this.set(key, result, time, unit);
        }

        return result;
    }


}
