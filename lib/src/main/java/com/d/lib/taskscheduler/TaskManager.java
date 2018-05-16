package com.d.lib.taskscheduler;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * TaskManager
 */
public class TaskManager {
    private static TaskManager ins;
    private Handler mainHandler;
    private ExecutorService cachedThreadPool = Executors.newCachedThreadPool();

    private TaskManager() {
        mainHandler = new Handler(Looper.getMainLooper());
    }

    public static TaskManager getIns() {
        if (ins == null) {
            synchronized (TaskManager.class) {
                if (ins == null) {
                    ins = new TaskManager();
                }
            }
        }
        return ins;
    }

    /**
     * 执行主线程任务
     */
    public void executeMainTask(Runnable runnable) {
        mainHandler.post(runnable);
    }

    /**
     * 执行异步任务
     */
    public void executeTask(Runnable runnable) {
        cachedThreadPool.execute(runnable);
    }
}
