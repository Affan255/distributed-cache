package main.cache;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class StripedExecutorService extends AbstractExecutorService {
    private int numStripes;
    private ExecutorService[] executors;
    private AtomicBoolean isShutdown = new AtomicBoolean(false);

    public StripedExecutorService(int numStripes) {
        this.numStripes = numStripes;
        this.executors = new ExecutorService[numStripes];
        for(int i=0;i<numStripes;i++)
            executors[i] = Executors.newSingleThreadExecutor();
    }

    @Override
    public void shutdown() {
        if(isShutdown.compareAndSet(false,true)) {
            for (ExecutorService executor : executors)
                executor.shutdown();
        }
    }

    @Override
    public List<Runnable> shutdownNow() {
        throw new UnsupportedOperationException("StripedExecutorService does not support shutdownNow");
    }

    @Override
    public boolean isShutdown() {
        return isShutdown.get();
    }

    @Override
    public boolean isTerminated() {
        boolean terminated=true;
        for(ExecutorService executor: executors){
            terminated = terminated &&  executor.isTerminated();
        }
        return terminated;
    }

    @Override
    public boolean awaitTermination(long l, TimeUnit timeUnit) throws InterruptedException {
       long timeoutMillis = timeUnit.toMillis(l)/numStripes;

       boolean terminated = true;
       for(ExecutorService executor: executors){
           terminated = terminated && executor.awaitTermination(timeoutMillis, TimeUnit.MILLISECONDS);
       }
       return terminated;
    }

    @Override
    public void execute(Runnable runnable) {
        throw new UnsupportedOperationException("Direct task execution is not supported by StripedExecutorService.");
    }

     public <E,T> CompletableFuture<T> submit(E key, Callable<T> task) {
        int stripe = key.hashCode() % numStripes;
        CompletableFuture<T> result = new CompletableFuture<>();
        executors[stripe].submit(() -> {
            try {
                result.complete(task.call());
            } catch (Exception e) {
                result.completeExceptionally(e);
            }
        });
        return result;
    }
}
