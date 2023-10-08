package test;

import main.cache.CacheNode;
import main.cache.DistributedCache;
import main.evictionpolicies.EvictionStrategyType;
import main.storage.StorageType;
import main.writepolicies.WriteStrategyType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertNull;

public class DistributedCacheTest {
    private DistributedCache<String, String> distributedCache;

    @BeforeEach
    public void setup(){
        int numReplicas = 3;
        int numNodes = 5;
        int cacheCapacity = 1500;
        List<CacheNode<String,String>> nodes = new ArrayList<>();
        for(int i=0;i<numNodes;i++){
            nodes.add(new CacheNode<>("node" + i, cacheCapacity, WriteStrategyType.WRITE_BACK, StorageType.MYSQL, EvictionStrategyType.LRU, 16));
        }
        distributedCache = new DistributedCache<>(numReplicas, nodes);
    }

    @Test
    public void testPutAndGet() throws ExecutionException, InterruptedException {
        distributedCache.putAsync("1","value");
        Future<String> valueFuture = distributedCache.getAsync("1");
        assert valueFuture.get().equals("value");

        distributedCache.putAsync("1","value2");
        Future<String> valueFuture2 = distributedCache.getAsync("1");
        assert valueFuture2.get().equals("value2");

    }

    @Test
    public void testDelete() throws ExecutionException, InterruptedException {
        distributedCache.putAsync("1","value");
        Future<Void> deleteFuture = distributedCache.deleteAsync("1");
        deleteFuture.get();
        Future<String> getFuture = distributedCache.getAsync("1");
        assertNull(getFuture.get());
    }

    @Test
    public void testAddAndRemoveNode() throws ExecutionException, InterruptedException {
        CacheNode<String,String> node = new CacheNode<>("newNode",5, WriteStrategyType.WRITE_THROUGH, StorageType.MYSQL, EvictionStrategyType.LRU,16);
        distributedCache.addNode(node);

        distributedCache.putAsync("1","value");
        Future<String> valueFuture = distributedCache.getAsync("1");
        assert valueFuture.get().equals("value");

        distributedCache.removeNode(node);

    }
}
