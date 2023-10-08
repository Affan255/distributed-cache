package main.evictionpolicies;

import main.cache.CacheEntry;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

public class LFUEvictionStrategy<K,V> implements EvictionStrategy<K,V> {

    private static class Pair<K,V>{
        private Integer frequency;
        private CacheEntry<K,V> entry;
        private Pair(Integer frequency, CacheEntry<K,V> entry){
            this.frequency = frequency;
            this.entry = entry;
        }

        @Override
        public String toString() {
            return "{" +
                    "frequency=" + frequency +
                    ", entry=" + entry +
                    '}';
        }
    }

    private Map<K,Pair<K,V>> lfuCache;
    private Map<Integer, LinkedHashSet<K>> frequencies;
    private int minFrequency;

    public LFUEvictionStrategy() {
        this.lfuCache = new HashMap<>();
        this.frequencies = new HashMap<>();
    }

    @Override
    public CacheEntry<K,V> evict(Map<K,CacheEntry<K,V>> cache) {

        if(!frequencies.containsKey(minFrequency) || frequencies.get(minFrequency).isEmpty())
            return null;
        K key = frequencies.get(minFrequency).iterator().next();
        frequencies.get(minFrequency).remove(key);
        if (frequencies.get(minFrequency).isEmpty())
            frequencies.remove(minFrequency);
        lfuCache.remove(key);
        return cache.remove(key);
    }

    @Override
    public void update(CacheEntry<K, V> entry) {

        if(!lfuCache.containsKey(entry.getKey())){
            lfuCache.put(entry.getKey(), new Pair<>(1, entry));
            if(!frequencies.containsKey(1))
                frequencies.put(1, new LinkedHashSet<>());
            frequencies.get(1).add(entry.getKey());
            minFrequency=1;
            return;
        }
        int frequency = lfuCache.get(entry.getKey()).frequency;
        lfuCache.put(entry.getKey(), new Pair<>(frequency+1,entry));

        frequencies.get(frequency).remove(entry.getKey());
        if(frequencies.get(frequency).isEmpty()) {
            frequencies.remove(frequency);
            if(minFrequency==frequency)
                minFrequency=frequency+1;
        }
        if(!frequencies.containsKey(frequency+1))
            frequencies.put(frequency+1, new LinkedHashSet<>());
        frequencies.get(frequency+1).add(entry.getKey());
    }

    public Map<K, Pair<K, V>> getLfuCache() {
        return lfuCache;
    }
}
