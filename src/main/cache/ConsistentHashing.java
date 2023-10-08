package main.cache;

import java.util.List;
import java.util.TreeMap;

public class ConsistentHashing<K,V> {
    private int numReplicas;
    private final TreeMap<Integer, CacheNode<K,V>> circle = new TreeMap<>();

    public ConsistentHashing(int numReplicas, List<CacheNode<K,V>> nodes){
        this.numReplicas = numReplicas;
        for(CacheNode<K,V> node: nodes)
            add(node);
    }

    public synchronized void add(CacheNode<K,V> node){
        for(int i=0;i<numReplicas;i++)
            circle.put((node.getNodeId()+i).hashCode(),node);
    }

    public synchronized void remove(CacheNode<K,V> node){
        for (int i=0;i<numReplicas;i++)
            circle.remove((node.getNodeId()+i).hashCode());
    }

    public CacheNode<K,V> get(K key){
        if(circle.isEmpty())
            return null;
        int hash = key.hashCode();
        if(!circle.containsKey(hash))
            hash = circle.ceilingKey(hash)!=null ? circle.ceilingKey(hash) : circle.firstKey();

        return circle.get(hash);
    }
}
