package com.d.lib.taskscheduler;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

/**
 * TaskManager
 */
public class TaskManager {
    private Handler mMainHandler;
    private ExecutorService mCachedThreadPool;
    private ExecutorService mSingleThreadExecutor;

    private TaskManager() {
        mMainHandler = new Handler(Looper.getMainLooper());
        mCachedThreadPool = Executors.newCachedThreadPool();
        mSingleThreadExecutor = Executors.newSingleThreadExecutor();
    }

    static TaskManager getInstance() {
        return Singleton.INSTANCE;
    }

    /**
     * Causes the Runnable command to be added to the message queue.
     * The runnable will be run in the main thread
     */
    boolean postMain(Runnable command) {
        return mMainHandler.post(command);
    }

    /**
     * Causes the Runnable command to be added to the message queue.
     * The runnable will be run in the main thread
     */
    boolean postMainDelayed(Runnable command, long delayMillis) {
        return mMainHandler.postDelayed(command, delayMillis);
    }

    /**
     * Execute sync task in the main thread
     */
    void executeMain(Runnable command) {
        if (Looper.getMainLooper().getThread() == Thread.currentThread()) {
            if (command != null) {
                command.run();
            }
            return;
        }
        mMainHandler.post(command);
    }

    /**
     * Execute async task in the cached thread pool
     *
     * @param command the runnable task
     * @throws RejectedExecutionException if this task cannot be accepted for execution
     * @throws NullPointerException       if command is null
     */
    void executeTask(Runnable command) {
        mCachedThreadPool.execute(command);
    }

    /**
     * Execute async task in the single thread pool
     *
     * @param command the runnable task
     * @throws RejectedExecutionException if this task cannot be accepted for execution
     * @throws NullPointerException       if command is null
     */
    void executeSingle(Runnable command) {
        mSingleThreadExecutor.execute(command);
    }

    /**
     * Execute async task in a new thread
     *
     * @param command the object whose {@code run} method is invoked when this thread
     *                is started. If {@code null}, this classes {@code run} method does
     *                nothing.
     */
    void executeNew(Runnable command) {
        new Thread(command).start();
    }

    private static class Singleton {
        private static final TaskManager INSTANCE = new TaskManager();
    }
}
