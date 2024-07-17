package com.ssc.taskexecutor;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.locks.ReentrantLock;

public class TaskExecutorService implements Main.TaskExecutor {
    private ExecutorService executorService;
    private Map<Main.TaskGroup, ReentrantLock> groupLockMap = new HashMap<>();

    public TaskExecutorService() {
        executorService = Executors.newFixedThreadPool(5);
    }

    @Override
    public <T> Future<Main.TaskResult<T>> submitTask(Main.Task<T> task) {

        ReentrantLock lock = groupLockMap.computeIfAbsent(task.taskGroup(), k -> new ReentrantLock(true));


        return executorService.submit(() -> {
            Timestamp startTime = new Timestamp(System.nanoTime());

            System.out.println("Started Executing task->" + task.taskUUID());
            lock.lock();
            try {
                return new Main.TaskResult<T>(startTime, task.taskAction().call());
            } finally {
                lock.unlock();
            }
        });
    }

    public void shoutDown() {
        executorService.shutdown();
    }
}
