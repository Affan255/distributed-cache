package main.writepolicies;

import main.storage.Database;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class WriteBackStrategy<K,V> implements WriteStrategy<K,V>{

    private enum Operation{
        ADD,
        UPDATE,
        DELETE
    }

    private Map<K,V> dirtyBitMap;
    private Map<K, Operation> operationMap;
    private ScheduledExecutorService scheduler;
    private Database<K,V> database;

    public WriteBackStrategy(Database<K,V> database){
        dirtyBitMap = new HashMap<>();
        operationMap = new HashMap<>();
        scheduler = Executors.newSingleThreadScheduledExecutor();
        this.database = database;
        scheduler.schedule(this::writeToDB,5, TimeUnit.SECONDS);
    }

    private synchronized void writeToDB() {
        for(Map.Entry<K,V> entry: dirtyBitMap.entrySet()){
            Operation operation = operationMap.get(entry.getKey());
            switch (operation){
                case ADD:
                    database.add(entry.getKey(), entry.getValue());
                    break;
                case UPDATE:
                    database.update(entry.getKey(), entry.getValue());
                    break;
                case DELETE:
                    database.delete(entry.getKey());
                    break;
            }
        }
        dirtyBitMap.clear();
        operationMap.clear();
    }

    @Override
    public synchronized void add( K key, V value) {
        dirtyBitMap.put(key,value);
        operationMap.put(key,Operation.ADD);
    }

    @Override
    public synchronized void update( K key, V value) {
        dirtyBitMap.put(key,value);
        operationMap.put(key,Operation.UPDATE);
    }

    @Override
    public V get( K key) {
        return null;
    }

    @Override
    public synchronized void delete( K key) {
        dirtyBitMap.put(key,null);
        operationMap.put(key, Operation.DELETE);
    }
}
