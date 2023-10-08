package main.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

public class RequestCollapser<K,V> {
    private Map<K, Future<V>> requestMap;

    public RequestCollapser(){
        requestMap = new ConcurrentHashMap<>();
    }

    public Future<V> collapse(K key, Future<V> newRequest){
        Future<V> existingRequest = requestMap.putIfAbsent(key, newRequest);
        return existingRequest == null ? newRequest:existingRequest;
    }

    public void remove(K key){
        requestMap.remove(key);
    }
}
