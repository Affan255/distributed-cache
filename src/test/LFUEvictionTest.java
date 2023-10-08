package test;

import main.cache.CacheEntry;
import main.evictionpolicies.LFUEvictionStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class LFUEvictionTest {
    private Map<Integer, CacheEntry<Integer, String>> cache;
    private LFUEvictionStrategy<Integer, String> lfuEvictionStrategy;
    private final int cacheCapacity = 3;

    @BeforeEach
    public void setup(){
        cache = new HashMap<>();
        lfuEvictionStrategy = new LFUEvictionStrategy<>();
    }


    private CacheEntry<Integer,String> putInCacheMock (Integer key, String value) {
        CacheEntry<Integer,String> evictedEntry = null;
        if (!cache.containsKey(key) && cache.size()==cacheCapacity)
            evictedEntry = lfuEvictionStrategy.evict(cache);

        CacheEntry<Integer,String> entry = new CacheEntry<>(key,value);
        cache.put(entry.getKey(),entry );

        lfuEvictionStrategy.update(entry);
        return evictedEntry;
    }

    private String getFromCacheMock(Integer key){
        lfuEvictionStrategy.update(cache.get(key));
        return cache.get(key).getValue();
    }


    @Test
    @DisplayName("Test with different frequency for each key with all put")
    public void testEvict1(){
        putInCacheMock(1,"User1");
        putInCacheMock(2,"User2");
        putInCacheMock(3,"User3");
        putInCacheMock(3,"User3");
        putInCacheMock(3,"User3");
        putInCacheMock(2,"User2");
        CacheEntry<Integer,String> evicted =putInCacheMock(4,"User4");
        assert evicted.getKey()==1 && evicted.getValue().equals("User1");
    }

    @Test
    @DisplayName("Test with different frequency for each key with get")
    public void testEvict2(){
        putInCacheMock(1,"User1");
        putInCacheMock(2,"User2");
        putInCacheMock(3,"User3");
        putInCacheMock(3,"User3");
        getFromCacheMock(3);
        getFromCacheMock(2);
        CacheEntry<Integer,String> evicted =putInCacheMock(4,"User4");
        assert evicted.getKey()==1 && evicted.getValue().equals("User1");
    }

    @Test
    @DisplayName("Test with same frequency for some keys with all put")
    public void testEvict3(){
        putInCacheMock(1,"User1");
        putInCacheMock(2,"User2");
        putInCacheMock(3,"User3");
        putInCacheMock(3,"User3");
        putInCacheMock(2,"User2");
        putInCacheMock(1,"User1");
        putInCacheMock(3,"User3");

        CacheEntry<Integer,String> evicted =putInCacheMock(4,"User4");
        assert evicted.getKey()==2 && evicted.getValue().equals("User2");
    }


    @Test
    @DisplayName("Test for lfuCache entries and size")
    public void testEvict5(){
        putInCacheMock(1,"User1");
        putInCacheMock(2,"User2");
        putInCacheMock(3,"User3");
        getFromCacheMock(1);
        getFromCacheMock(3);
        CacheEntry<Integer,String> entry = putInCacheMock(4,"User4");
        assert lfuEvictionStrategy.getLfuCache().size()==3;
        assert entry.getKey()==2;
        entry = putInCacheMock(5,"User5");
        assert lfuEvictionStrategy.getLfuCache().size()==3;
        assert entry.getKey()==4;
        System.out.println(lfuEvictionStrategy.getLfuCache());

    }
}

