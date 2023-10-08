package main.storage;

public class MySqlDatabase<K,V> implements Database<K,V>{

    @Override
    public V get(K key) {
        return null;
    }

    @Override
    public void add(K key, V value) {
        System.out.println(Thread.currentThread().getName() + " Add to MySql database -> " + key +": "+value);
    }

    @Override
    public void update(K key, V value) {
        System.out.println(Thread.currentThread().getName() + " Update to MySql database -> " + key +": "+value);
    }

    @Override
    public void delete(K key) {
        System.out.println(Thread.currentThread().getName() + " Delete from MySql database -> " + key);

    }
}
