package main.evictionpolicies;

import main.cache.CacheEntry;

import java.util.Map;

public interface EvictionStrategy<K,V> {
    CacheEntry<K,V> evict(Map<K,CacheEntry<K,V>> cache);
    void update(CacheEntry<K,V> entry);
}
