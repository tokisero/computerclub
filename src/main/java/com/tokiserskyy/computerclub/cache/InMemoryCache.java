package com.tokiserskyy.computerclub.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class InMemoryCache<K, V> {
    private static final Logger logger = LoggerFactory.getLogger(InMemoryCache.class);
    private final Map<K, CacheEntry<V>> cache;
    private static final int MAX_ENTRIES = 300;

    public InMemoryCache() {
        this.cache = new LinkedHashMap<>(MAX_ENTRIES, 0.75f, false) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, CacheEntry<V>> eldest) {
                boolean shouldRemove = size() > MAX_ENTRIES;
                if (shouldRemove) {
                    logger.debug("Removing eldest entry with key: {} due to cache size limit ({})", eldest.getKey(), MAX_ENTRIES);
                }
                return shouldRemove;
            }
        };
        logger.info("InMemoryCache initialized with max entries: {}", MAX_ENTRIES);
    }

    public synchronized void put(K key, V value, long ttlMillis) {
        if (key == null) {
            logger.warn("Attempt to put entry with null key");
            return;
        }
        logger.debug("Putting entry into cache - key: {}, TTL: {}ms", key, ttlMillis);
        cache.put(key, new CacheEntry<>(value, ttlMillis));
        logger.trace("Cache size after put: {}", cache.size());
    }

    public synchronized Optional<V> get(K key) {
        logger.debug("Getting entry from cache - key: {}", key);
        if (isEntryInvalid(key)) {
            return Optional.empty();
        }
        V value = cache.get(key).getValue();
        logger.debug("Cache hit - key: {}", key);
        return Optional.ofNullable(value);
    }

    public synchronized <T> Optional<T> getById(K key, int id) {
        logger.debug("Getting object by ID from cache - key: {}, ID: {}", key, id);
        if (isEntryInvalid(key)) {
            return Optional.empty();
        }

        V value = cache.get(key).getValue();
        if (!(value instanceof List)) {
            logger.debug("Cache entry is not a list - key: {}", key);
            return Optional.empty();
        }

        @SuppressWarnings("unchecked")
        List<T> list = (List<T>) value;
        Optional<T> result = list.stream()
                .filter(item -> item instanceof HasId && ((HasId) item).getId() == id)
                .findFirst();

        logger.debug(result.isPresent() ?
                        "Found object in cache - key: {}, ID: {}" :
                        "Object not found in cache - key: {}, ID: {}",
                key, id);
        return result;
    }

    public synchronized void remove(K key) {
        logger.debug("Removing entry from cache - key: {}", key);
        cache.remove(key);
        logger.trace("Cache size after remove: {}", cache.size());
    }

    public synchronized int size() {
        int size = cache.size();
        logger.trace("Current cache size: {}", size);
        return size;
    }

    private boolean isEntryInvalid(K key) {
        if (key == null) {
            logger.warn("Attempt to check null key");
            return true;
        }

        CacheEntry<V> entry = cache.get(key);
        if (entry == null) {
            logger.debug("Cache miss - key not found: {}", key);
            return true;
        }

        if (entry.isExpired()) {
            logger.debug("Cache entry expired - key: {}", key);
            cache.remove(key);
            return true;
        }

        return false;
    }

    public synchronized Map<K, CacheEntry<V>> getCache() {
        logger.trace("Cache content requested. Current size: {}", cache.size());
        return cache;
    }

    public interface HasId {
        int getId();
    }
}