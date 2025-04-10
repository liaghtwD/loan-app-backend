package cyou.oxling.loanappbackend.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.UUID;

@Component
public class RedisUtil {

    @Autowired
    private StringRedisTemplate redisTemplate;

    public void set(String key, String value, long timeout) {
        redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
    }

    public void set(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public String get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void delete(String key) {
        redisTemplate.delete(key);
    }

    // 新增方法：生成一次性下载链接
    public String generateDownloadLink(String fileId, String filePath, long expirationTime) {
        String uuid = UUID.randomUUID().toString();
        String key = "download_link:" + uuid;
        set(key, filePath, expirationTime); // 将文件路径存入Redis，设置过期时间
        return uuid; // 返回UUID作为下载链接
    }

    public void cacheFilePath(String fileId, String filePath, long expirationTime) {
        String key = "file_path:" + fileId;
        set(key, filePath, expirationTime); // 将文件路径存入Redis，设置过期时间
    }

    public void deleteCachedFilePath(String fileId) {
        String key = "file_path:" + fileId;
        delete(key); // 从Redis中删除文件路径缓存
    }
}