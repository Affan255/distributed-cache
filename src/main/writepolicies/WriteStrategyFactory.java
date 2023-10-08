package main.writepolicies;

import main.storage.Database;

public class WriteStrategyFactory<K,V> {

    public WriteStrategy<K,V> getWriteStrategyFromType(WriteStrategyType type, Database<K,V> database){
        switch (type){
            case WRITE_BACK: return new WriteBackStrategy<>(database);
            case WRITE_THROUGH: return new WriteThroughStrategy<>(database);
        }
        return null;
    }
}
