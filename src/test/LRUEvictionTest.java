package test;

import main.cache.CacheEntry;
import main.evictionpolicies.LRUEvictionStrategy;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class LRUEvictionTest {
    private Map<Integer, CacheEntry<Integer, String>> cache;
    private LRUEvictionStrategy<Integer, String> lruEvictionStrategy;
    private final int cacheCapacity = 3;

    @BeforeEach
    public void setup(){
        cache = new HashMap<>();
        lruEvictionStrategy = new LRUEvictionStrategy<>();
    }


    private CacheEntry<Integer,String> putInCacheMock (Integer key, String value) {
        CacheEntry<Integer,String> evictedEntry = null;
        if (!cache.containsKey(key) && cache.size()==cacheCapacity)
            evictedEntry = lruEvictionStrategy.evict(cache);

        CacheEntry<Integer,String> entry = new CacheEntry<>(key,value);
        cache.put(entry.getKey(),entry );

        lruEvictionStrategy.update(entry);
        return evictedEntry;
    }

    private String getFromCacheMock(Integer key){
        lruEvictionStrategy.update(cache.get(key));
        return cache.get(key).getValue();
    }


    @Test
    @DisplayName("Test with only put calls")
    public void testEvict1(){
        putInCacheMock(1,"User1");
        putInCacheMock(2,"User2");
        putInCacheMock(3,"User3");
        CacheEntry<Integer,String> evicted =putInCacheMock(4,"User4");
        assert evicted.getKey()==1 && evicted.getValue().equals("User1");
    }

    @Test
    @DisplayName("Test with one get call")
    public void testEvict2(){
        putInCacheMock(1,"User1");
        putInCacheMock(2,"User2");
        putInCacheMock(3,"User3");
        getFromCacheMock(1);
        CacheEntry<Integer,String> evicted =putInCacheMock(4,"User4");
        assert evicted.getKey()==2 && evicted.getValue().equals("User2");
    }

    @Test
    @DisplayName("Test for lruCache entries and size")
    public void testEvict3(){
        putInCacheMock(1,"User1");
        putInCacheMock(2,"User2");
        putInCacheMock(3,"User3");
        getFromCacheMock(1);
        putInCacheMock(4,"User4");
        putInCacheMock(5,"User5");
        System.out.println(lruEvictionStrategy.getLruMap());
        assert lruEvictionStrategy.getLruMap().size()==3;
    }
}
