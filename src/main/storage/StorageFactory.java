package main.storage;

public class StorageFactory {

    public <K,V> Database<K,V> getStorageFromType(StorageType type){
        switch (type){
            case MYSQL: return new MySqlDatabase<>();
            case POSTGRES: return new PostgresDatabase<>();
        }
        return null;
    }
}
