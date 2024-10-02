package hu.ag.sse.redis.dcache;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RedisCacheService {

    @Value("${dcache.connection.info}")
    private String distributedCacheConnectionInfo;

    @Autowired
    private CacheManager cacheManager;

    public void addConnectionInfo(String key, String value) {
        log.info("addConnectionInfo: {} {}", key, value);
        Cache cache = cacheManager.getCache(distributedCacheConnectionInfo);
        if (cache != null) {
            cache.put(key, value);
            log.info("added to cache {} {}", distributedCacheConnectionInfo, key);
        }
    }

    public Object getCacheValue(String key) {
        log.info("getCacheValue for key: [{}]", key);
        Cache cache = cacheManager.getCache(distributedCacheConnectionInfo);
        return cache.get(key);
    }

    public void removeCacheEntry(String key) {
        log.info("removeCacheEntry: {}", key);
        Cache cache = cacheManager.getCache(distributedCacheConnectionInfo);
        if (cache != null) {
            cache.evictIfPresent(key);
            log.info("removed from cache {} {}", distributedCacheConnectionInfo, key);
        }
    }
}
