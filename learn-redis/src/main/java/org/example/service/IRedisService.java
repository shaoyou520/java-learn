package org.example.service;

import org.springframework.data.redis.core.RedisCallback;

import java.util.Collection;
import java.util.Set;

public interface IRedisService<T> {

    /**
     * 设置
     * @param key
     * @param value
     */
    void set(String key, T value);

    /**
     * 设置并设置超时时间,(秒)
     * @param key
     * @param value
     * @param time
     */
    void set(String key, T value, long time);

    /**
     * 查询
     * @param key
     * @return
     */
    T get(String key);

    /**
     * 删除
     * @param key
     */
    void delete(String key);

    /**
     * 删除
     * @param keys
     */
    void delete(Collection<String> keys);

    /**
     * 设置超时时间
     * @param key
     * @param time
     * @return
     */
    boolean expire(String key, long time);

    /**
     * 获取超时时间
     * @param key
     * @return
     */
    Long getExpire(String key);

    /**
     *  是否存在
     * @param key
     * @return
     */
    boolean hasKey(String key);

    /**
     * 通过 delta 增加以字符串值形式存储在键下的浮点数值。
     * @param key
     * @param delta
     * @return
     */
    Long increment(String key, long delta);

    /**
     * 通过 delta 减少以字符串值形式存储在键下的浮点数值。
     * @param key
     * @param delta
     * @return
     */
    Long decrement(String key, long delta);

    /**
     * 添加set
     * @param key
     * @param value
     */
    void addSet(String key, T value);

    /**
     * 添加set, 并设置排序分数
     * @param key
     * @param value
     * @param score
     */
    void addSet(String key, T value, Double score);

    /**
     * 获取set
     * @param key
     * @return
     */
    Set<T> getSet(String key);

    /**
     * 删除set
     * @param key
     * @param value
     */
    void deleteSet(String key, T value);

    /**
     * 在 Redis 连接中执行给定的操作
     * @param redisCallback
     * @return
     */
    T execute(RedisCallback<T> redisCallback);
}
