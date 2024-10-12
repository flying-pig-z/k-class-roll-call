package com.flyingpig.kclassrollcall.util.cache;

import com.alibaba.fastjson.JSON;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class ListCacheUtil {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    public <T> void set(String key, List<T> values, Long time, TimeUnit unit) {
        // 使用事务确保原子性
        stringRedisTemplate.execute((RedisCallback<Void>) connection -> {
            // 清空现有列表内容
            stringRedisTemplate.opsForList().trim(key, 0, -1);
            // 将列表中的每个值存储到 Redis 列表中
            values.forEach(value -> stringRedisTemplate.opsForList().rightPush(key, JSON.toJSONString(value)));
            // 设置列表的过期时间
            stringRedisTemplate.expire(key, time, unit);
            return null;
        });
    }

    public <T> List<T> get(String key, Class<T> type) {
        List<String> value = stringRedisTemplate.opsForList().range(key, 0, -1);
        return (value == null || value.isEmpty()) ? null : value.stream()
                .map(json -> JSON.parseObject(json, type))
                .collect(Collectors.toList());
    }

    public <T> List<T> safeGetWithLock(String key, Class<T> type, CacheLoader<List<T>> cacheLoader, Long time, TimeUnit unit) {
        List<T> cachedValues = get(key, type);

        // 1. 命中且不为空，直接返回; 命中却为空，返回 null
        if (cachedValues != null) {
            return cachedValues;
        }

        // 获取锁
        String lockKey = "lock:" + key;
        RLock rLock = redissonClient.getLock(lockKey);
        List<T> result = null;

        try {
            if (rLock.tryLock()) { // 尝试获取锁，避免死锁
                // 再次查询 Redis，双重判定
                cachedValues = get(key, type);
                if (cachedValues != null) {
                    return cachedValues;
                }

                // 获取锁成功，查询数据库
                result = loadAndSet(key, cacheLoader, time, unit);
            }
        } catch (Exception e) {
            // 记录日志
            // logger.error("Error occurred while accessing cache with key: {}", key, e);
        } finally {
            // 确保释放锁
            if (rLock.isHeldByCurrentThread()) {
                rLock.unlock();
            }
        }

        return result;
    }

    private <T> List<T> loadAndSet(String key, CacheLoader<List<T>> cacheLoader, Long time, TimeUnit unit) {
        // 获取锁成功，查询数据库
        List<T> result = cacheLoader.load(); // 返回 List<T>

        if (result == null || result.isEmpty()) {
            // 将空值写入 Redis，返回 null
            stringRedisTemplate.opsForList().rightPush(key, ""); // 存储一个空字符串作为占位符
            stringRedisTemplate.expire(key, time, unit);
            return null;
        }

        // 存在，写入 Redis
        set(key, result, time, unit);
        return result;
    }

    public ListOperations<String, String> getListOperations() {
        return stringRedisTemplate.opsForList();
    }
}
