package main.cache;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public class DistributedCache<K,V> {

    private ConsistentHashing<K,V> consistentHashing;
    private RequestCollapser<K,V> requestCollapser;


    public DistributedCache(int numReplicas, List<CacheNode<K,V>> nodes){
        this.consistentHashing = new ConsistentHashing<>(numReplicas,nodes);
        this.requestCollapser = new RequestCollapser<>();
    }

    public CompletableFuture<Void> putAsync(K key, V value){
        CacheNode<K,V> node = consistentHashing.get(key);
        if(node!=null){
            return node.putAsync(key,value);
        }
        return CompletableFuture.completedFuture(null);
    }

    public Future<V> getAsync(K key){
        CacheNode<K,V> node = consistentHashing.get(key);
        if(node!=null){
            Future<V> futureValue = node.getAsync(key);
            requestCollapser.remove(key);
            Future<V> collapsedFuture = requestCollapser.collapse(key, futureValue);
            return collapsedFuture;
        }
        return CompletableFuture.completedFuture(null);
    }

    public CompletableFuture<Void> deleteAsync(K key){
        CacheNode<K,V> node = consistentHashing.get(key);
        if(node != null){
            return node.deleteAsync(key);
        }
        return CompletableFuture.completedFuture(null);
    }

    public void addNode(CacheNode<K,V> node){
        consistentHashing.add(node);
    }

    public void removeNode(CacheNode<K,V> node){
        consistentHashing.remove(node);
    }

    public CacheNode<K,V> getNode(K key){
        return consistentHashing.get(key);
    }
}
