package com.tokiserskyy.computerclub.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CacheEntry<V> {
    private static final Logger logger = LoggerFactory.getLogger(CacheEntry.class);
    private final V value;
    private final long expiryTime;

    public CacheEntry(V value, long ttlMillis) {
        this.value = value;
        this.expiryTime = System.currentTimeMillis() + ttlMillis;
        logger.debug("Created cache entry with TTL: {}ms", ttlMillis);
    }

    public V getValue() {
        if (isExpired()) {
            logger.warn("Attempted to retrieve expired cache entry");
            return null;
        }
        logger.debug("Retrieved cache entry value");
        return value;
    }

    public boolean isExpired() {
        boolean expired = System.currentTimeMillis() > expiryTime;
        return expired;
    }

    public long getTtlMillis() {
        return expiryTime;
    }
}