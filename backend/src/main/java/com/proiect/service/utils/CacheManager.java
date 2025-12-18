package com.proiect.service.utils;
import com.github.benmanes.caffeine.cache.*;
import org.springframework.stereotype.Service;

@Service
public class CacheManager<T> {
    private Cache<String, T> cache = Caffeine.newBuilder().expireAfterWrite(24, java.util.concurrent.TimeUnit.HOURS).build();

    /**
     * Obtine valoarea din cache.
     * @param key cheia cache
     * @return valoarea cache-uita sau null
     */
    public T get(String key) {
        return cache.getIfPresent(key);
    }
    /**
     * Pune valoarea in cache.
     * @param key cheia cache
     * @param value valoarea de cache-uit
     */
    public void put(String key, T value) {
        cache.put(key, value);
    }
}
