package main.writepolicies;

import main.storage.Database;

public class WriteThroughStrategy<K,V> implements WriteStrategy<K,V>{

    private Database<K,V> database;

    public WriteThroughStrategy(Database<K, V> database) {
        this.database = database;
    }

    @Override
    public void add( K key, V value) {
        database.add(key,value);
    }

    @Override
    public void update(K key, V value) {
        database.update(key,value);
    }

    @Override
    public V get( K key) {
        return null;
    }

    @Override
    public void delete( K key) {
        database.delete(key);
    }
}
