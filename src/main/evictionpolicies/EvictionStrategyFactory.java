package main.evictionpolicies;

public class EvictionStrategyFactory<K,V> {

    public EvictionStrategy<K,V> getEvictionStrategyFromType(EvictionStrategyType type){
        switch (type){
            case LFU: return new LFUEvictionStrategy<>();
            case LRU: return new LRUEvictionStrategy<>();
        }
        return null;
    }
}
