package main.storage;

public interface Database<K,V> {
    V get(K key);
    void add(K key, V value);
    void update(K key, V value);
    void delete(K key);
}
