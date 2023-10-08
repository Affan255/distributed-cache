package main.cache;

import main.evictionpolicies.EvictionStrategyType;
import main.storage.StorageType;
import main.writepolicies.WriteStrategyType;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

public class CacheClient<K,V> {

    private final DistributedCache<K,V> cache;

    public CacheClient(DistributedCache<K,V> cache){
        this.cache = cache;
    }

    public Future<Void> putAsync(K key, V value){
        return cache.putAsync(key,value);
    }

    public Future<V> getAsync(K key){
        return cache.getAsync(key);
    }

    public Future<Void> deleteAsync(K key) {
        return cache.deleteAsync(key);
    }

    public static void main(String[] args) {
        int numReplicas = 3;
        int numNodes = 5;
        int cacheCapacity = 1500;
        List<CacheNode<String,String>> nodes = new ArrayList<>();
        for(int i=0;i<numNodes;i++){
            nodes.add(new CacheNode<>("node" + i, cacheCapacity, WriteStrategyType.WRITE_THROUGH, StorageType.MYSQL, EvictionStrategyType.LRU, 16));
        }
        DistributedCache<String,String> distributedCache = new DistributedCache<>(numReplicas, nodes);

        CacheClient<String,String> client = new CacheClient<>(distributedCache);
        client.putAsync("1","value");
        client.putAsync("2","value2");
        client.putAsync("3","value3");
        client.putAsync("1","value4");
        client.deleteAsync("2");
    }
}
