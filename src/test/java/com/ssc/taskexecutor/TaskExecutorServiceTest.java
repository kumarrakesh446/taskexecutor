package com.ssc.taskexecutor;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

class TaskExecutorServiceTest {

    @Test
    void submitTask() throws ExecutionException, InterruptedException {
        TaskExecutorService executorService = new TaskExecutorService();

        Main.TaskGroup[] taskGroups = new Main.TaskGroup[]{new Main.TaskGroup(UUID.randomUUID()), new Main.TaskGroup(UUID.randomUUID()), new Main.TaskGroup(UUID.randomUUID())};

        Main.Task[] tasks = new Main.Task[10];
        Future<Main.TaskResult<Integer>>[] futures = new Future[10];

        for (int i = 0; i < 10; i++) {

            UUID taskId = UUID.randomUUID();
            int finalI = i;
            Main.Task task = new Main.Task(taskId, taskGroups[i % 3], Main.TaskType.READ, () -> {
                System.out.println("Executing Task-->" + finalI + "TaskID-" + taskId);
                Thread.sleep(1000);
                return finalI;
            });
            futures[i] = executorService.submitTask(task);
        }

        Main.TaskResult<Integer>[] taskResult = new Main.TaskResult[10];

        for (int i = 0; i < 10; i++) {
            taskResult[i] = futures[i].get();
        }
        Arrays.stream(taskResult).forEach(System.out::println);

        executorService.shoutDown();


    }
}