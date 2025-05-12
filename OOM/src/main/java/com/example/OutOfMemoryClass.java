package com.example;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OutOfMemoryClass {
    private static final Map<Integer, byte[]> STATIC_CACHE = new HashMap<>();


    static class Worker implements Runnable {
        private static final ThreadLocal<byte[]> threadLocalData = ThreadLocal.withInitial(() -> new byte[5 * 1024 * 1024]);

        private final int id;

        Worker(int id) {
            this.id = id;
        }

        @Override
        public void run() {
            byte[] bytes = threadLocalData.get();
            STATIC_CACHE.put(id, bytes);
            try{
                Thread.sleep(100);

            }catch (InterruptedException ignored){
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ExecutorService executorService = Executors.newCachedThreadPool();
        int iteration = 0;
        while(true){
            executorService.submit(new Worker(iteration++));
            if(++iteration % 10 == 0){
                System.out.printf("Submitted %d workers, cache size=%d%n", iteration, STATIC_CACHE.size());
            }
            Thread.sleep(50);
        }
    }
}
