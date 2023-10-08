package main.cache;

import main.evictionpolicies.EvictionStrategy;
import main.evictionpolicies.EvictionStrategyFactory;
import main.evictionpolicies.EvictionStrategyType;
import main.storage.Database;
import main.storage.StorageFactory;
import main.storage.StorageType;
import main.writepolicies.WriteStrategy;
import main.writepolicies.WriteStrategyFactory;
import main.writepolicies.WriteStrategyType;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;

public class CacheNode<K,V> {

    private String nodeId;
    private int capacity;
    private WriteStrategy<K,V> writeStrategy;
    private EvictionStrategy<K,V> evictionStrategy;
    private Map<K,CacheEntry<K,V>> cache;
    private StripedExecutorService executorService;
    private Database<K,V> database;

    public CacheNode(String nodeId, int capacity, WriteStrategyType writeStrategyType, StorageType storageType, EvictionStrategyType evictionStrategyType, int numStripes) {
        this.nodeId = nodeId;
        this.capacity = capacity;
        this.database = new StorageFactory().getStorageFromType(storageType);
        this.writeStrategy = new WriteStrategyFactory<K,V>().getWriteStrategyFromType(writeStrategyType, database);
        this.evictionStrategy = new EvictionStrategyFactory<K,V>().getEvictionStrategyFromType(evictionStrategyType);
        this.cache = new HashMap<>(capacity);
        this.executorService = new StripedExecutorService(numStripes);
    }

    public synchronized void put(K key, V value){
        if(!cache.containsKey(key) && cache.size()==capacity)
            evictionStrategy.evict(cache);
        CacheEntry<K,V> entry = new CacheEntry<>(key,value);
        boolean isUpdate = false;
        if(cache.containsKey(key))
            isUpdate=true;
        cache.put(key,entry);
        evictionStrategy.update(entry);
        // Write to database
        if(isUpdate)
            writeStrategy.update(key,value);
        else
            writeStrategy.add(key,value);
    }

    public synchronized V get(K key){
        if(!cache.containsKey(key)){
            // TODO: Get from database
            return null;
        }

        evictionStrategy.update(cache.get(key));
        return cache.get(key).getValue();
    }

    public synchronized void delete(K key){
        if(!cache.containsKey(key))
            return;

        cache.remove(key);
        writeStrategy.delete(key);
    }

    public CompletableFuture<V> getAsync(K key){
        return executorService.submit(key,() -> get(key));
    }

    public CompletableFuture<Void> putAsync(K key, V value){
        return executorService.submit(key, () -> {
            put(key, value);
            return null;
        });
    }

    public CompletableFuture<Void> deleteAsync(K key){
        return executorService.submit(key, () -> {
            delete(key);
            return null;
        });
    }

    public String getNodeId() {
        return nodeId;
    }
}
