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
        logger.debug("Putting entry into cache: key={}, value={}, ttl={}ms", key, value, ttlMillis);
        cache.put(key, new CacheEntry<>(value, ttlMillis));
        logger.trace("Cache size after put: {}", cache.size());
    }

    public synchronized Optional<V> get(K key) {
        logger.debug("Getting entry from cache: key={}", key);
        CacheEntry<V> entry = cache.get(key);
        if (entry == null) {
            logger.debug("Cache miss: key={} not found", key);
            return Optional.empty();
        }
        if (entry.isExpired()) {
            logger.debug("Cache entry expired: key={}", key);
            cache.remove(key);
            return Optional.empty();
        }
        logger.debug("Cache hit: key={}, value={}", key, entry.getValue());
        return Optional.ofNullable(entry.getValue());
    }

    public synchronized <T> Optional<T> getById(K key, int id) {
        logger.debug("Getting object by ID from cache: key={}, id={}", key, id);
        CacheEntry<V> entry = cache.get(key);
        if (entry == null) {
            logger.debug("Cache miss: key={} not found", key);
            return Optional.empty();
        }
        if (entry.isExpired()) {
            logger.debug("Cache entry expired: key={}", key);
            cache.remove(key);
            return Optional.empty();
        }
        V value = entry.getValue();
        if (value instanceof List) {
            @SuppressWarnings("unchecked")
            List<T> list = (List<T>) value;
            Optional<T> result = list.stream()
                    .filter(item -> item instanceof HasId && ((HasId) item).getId() == id)
                    .findFirst();
            if (result.isPresent()) {
                logger.debug("Cache hit: key={}, id={}, value={}", key, id, result.get());
            } else {
                logger.debug("Cache miss: id={} not found in list for key={}", id, key);
            }
            return result;
        }
        logger.debug("Cache miss: key={} does not contain a list", key);
        return Optional.empty();
    }

    public synchronized void remove(K key) {
        logger.debug("Removing entry from cache: key={}", key);
        cache.remove(key);
        logger.trace("Cache size after remove: {}", cache.size());
    }

    public synchronized <T> void removeById(K key, int id) {
        logger.debug("Removing object by ID from cache: key={}, id={}", key, id);
        CacheEntry<V> entry = cache.get(key);
        if (entry == null || entry.isExpired()) {
            logger.debug("Cache miss or expired: key={}", key);
            return;
        }
        V value = entry.getValue();
        if (value instanceof List) {
            @SuppressWarnings("unchecked")
            List<T> list = (List<T>) value;
            boolean removed = list.removeIf(item -> item instanceof HasId && ((HasId) item).getId() == id);
            if (removed) {
                logger.debug("Removed object with id={} from list for key={}", id, key);
                if (list.isEmpty()) {
                    cache.remove(key);
                    logger.debug("List is empty, removed cache entry for key={}", key);
                } else {
                    cache.put(key, new CacheEntry<>(value, entry.getTtlMillis()));
                    logger.trace("Updated cache entry for key={} after removal", key);
                }
            } else {
                logger.debug("Object with id={} not found in list for key={}", id, key);
            }
        } else {
            logger.debug("Cache entry for key={} is not a list", key);
        }
    }

    public synchronized void clear() {
        logger.info("Clearing entire cache. Size before clear: {}", cache.size());
        cache.clear();
        logger.trace("Cache cleared. Size after clear: {}", cache.size());
    }

    public synchronized int size() {
        int size = cache.size();
        logger.trace("Cache size requested: {}", size);
        return size;
    }

    public synchronized Map<K, CacheEntry<V>> getCache() {
        logger.trace("Cache content requested. Current size: {}", cache.size());
        return cache;
    }

    public interface HasId {
        int getId();
    }
}