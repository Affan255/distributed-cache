package main.evictionpolicies;

import main.cache.CacheEntry;

import java.util.LinkedHashMap;
import java.util.Map;

public class LRUEvictionStrategy<K,V> implements EvictionStrategy<K,V>{

    private LinkedHashMap<K, CacheEntry<K,V>> lruMap;

    public LRUEvictionStrategy() {
        lruMap = new LinkedHashMap<>();
    }

    @Override
    public CacheEntry<K,V> evict(Map<K,CacheEntry<K,V>> cache) {
        if(lruMap.isEmpty())
            return null;
        CacheEntry<K,V> entry = lruMap.entrySet().iterator().next().getValue();
        lruMap.remove(entry.getKey());
        return cache.remove(entry.getKey());
    }

    @Override
    public void update(CacheEntry<K, V> entry) {
        K key = entry.getKey();
        lruMap.remove(key);
        lruMap.put(key, entry);
    }

    public LinkedHashMap<K, CacheEntry<K, V>> getLruMap() {
        return lruMap;
    }
}
