package com.tokiserskyy.computerclub.cache;

import org.springframework.stereotype.Component;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class InMemoryCache<K, V> {
    private final Map<K, CacheEntry<V>> cache;
    private static final int MAX_ENTRIES = 300;
    
    public InMemoryCache() {
        this.cache = new LinkedHashMap<>(MAX_ENTRIES, 0.75f, false) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, CacheEntry<V>> eldest) {
                return size() > MAX_ENTRIES;
            }
        };
    }

    public synchronized void put(K key, V value, long ttlMillis) {
        cache.put(key, new CacheEntry<>(value, ttlMillis));
    }

    public synchronized Optional<V> get(K key) {
        CacheEntry<V> entry = cache.get(key);
        if (entry == null || entry.isExpired()) {
            cache.remove(key);
            return Optional.empty();
        }
        return Optional.ofNullable(entry.getValue());
    }

    public synchronized void remove(K key) {
        cache.remove(key);
    }

    public synchronized void clear() {
        cache.clear();
    }

    public synchronized int size() {
        return cache.size();
    }

    public synchronized Map<K, CacheEntry<V>> getCache() {
        return cache;
    }
}