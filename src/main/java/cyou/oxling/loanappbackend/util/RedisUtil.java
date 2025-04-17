package cyou.oxling.loanappbackend.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Redis工具类
 */
@Component
public class RedisUtil {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 设置String类型的值
     * @param key 键
     * @param value 值
     */
    public void setString(String key, String value) {
        stringRedisTemplate.opsForValue().set(key, value);
    }

    /**
     * 设置String类型的值，并指定过期时间
     * @param key 键
     * @param value 值
     * @param timeout 过期时间
     * @param unit 时间单位
     */
    public void setString(String key, String value, long timeout, TimeUnit unit) {
        stringRedisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    /**
     * 获取String类型的值
     * @param key 键
     * @return 值
     */
    public String getString(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    /**
     * 设置Hash类型的值
     * @param key 键
     * @param hashKey Hash键
     * @param value 值
     */
    public void setHash(String key, String hashKey, String value) {
        stringRedisTemplate.opsForHash().put(key, hashKey, value);
    }

    /**
     * 批量设置Hash类型的值
     * @param key 键
     * @param map Map对象
     */
    public void setHashAll(String key, Map<String, String> map) {
        HashOperations<String, Object, Object> hashOps = stringRedisTemplate.opsForHash();
        map.forEach((hashKey, value) -> hashOps.put(key, hashKey, value));
    }

    /**
     * 获取Hash类型的单个值
     * @param key 键
     * @param hashKey Hash键
     * @return 值
     */
    public String getHash(String key, String hashKey) {
        return (String) stringRedisTemplate.opsForHash().get(key, hashKey);
    }

    /**
     * 获取Hash类型的所有值
     * @param key 键
     * @return 值Map
     */
    public Map<Object, Object> getHashAll(String key) {
        return stringRedisTemplate.opsForHash().entries(key);
    }

    /**
     * 删除键
     * @param key 键
     */
    public void delete(String key) {
        stringRedisTemplate.delete(key);
    }

    /**
     * 删除Hash类型的键值
     * @param key 键
     * @param hashKey Hash键
     */
    public void deleteHash(String key, String hashKey) {
        stringRedisTemplate.opsForHash().delete(key, hashKey);
    }

    /**
     * 设置过期时间
     * @param key 键
     * @param timeout 过期时间
     * @param unit 时间单位
     */
    public void expire(String key, long timeout, TimeUnit unit) {
        stringRedisTemplate.expire(key, timeout, unit);
    }

    /**
     * 判断键是否存在
     * @param key 键
     * @return 是否存在
     */
    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(key));
    }

    /**
     * 判断Hash中键是否存在
     * @param key 键
     * @param hashKey Hash键
     * @return 是否存在
     */
    public boolean hasHashKey(String key, String hashKey) {
        return stringRedisTemplate.opsForHash().hasKey(key, hashKey);
    }
} 