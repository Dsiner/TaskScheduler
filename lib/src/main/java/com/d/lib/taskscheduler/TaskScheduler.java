package com.d.lib.taskscheduler;

import com.d.lib.taskscheduler.callback.Task;
import com.d.lib.taskscheduler.schedule.Schedulers;
import com.d.lib.taskscheduler.schedule.TaskEmitter;

/**
 * TaskScheduler
 * Created by D on 2018/5/15.
 */
public class TaskScheduler<T> {
    private Task<T> mTask;
    private int mSubscribeScheduler = Schedulers.defaultThread();

    private TaskScheduler() {
    }

    /**
     * Causes the Runnable r to be added to the message queue.
     * The runnable will be run in the main thread
     */
    public static boolean postMain(Runnable r) {
        return TaskManager.getInstance().postMain(r);
    }

    /**
     * Causes the Runnable r to be added to the message queue.
     * The runnable will be run in the main thread
     */
    public static boolean postMainDelayed(Runnable r, long delayMillis) {
        return TaskManager.getInstance().postMainDelayed(r, delayMillis);
    }

    /**
     * Execute sync task in the main thread
     */
    public static void executeMain(Runnable r) {
        TaskManager.getInstance().executeMain(r);
    }

    /**
     * Execute async task in the cached thread pool
     */
    public static void executeTask(Runnable r) {
        TaskManager.getInstance().executeTask(r);
    }

    /**
     * Execute async task in the single thread pool
     */
    public static void executeSingle(Runnable r) {
        TaskManager.getInstance().executeSingle(r);
    }

    /**
     * Execute async task in a new thread
     */
    public static void executeNew(Runnable r) {
        TaskManager.getInstance().executeNew(r);
    }

    /**
     * Create task
     */
    public static <T> TaskScheduler<T> create(final Task<T> task) {
        TaskScheduler<T> schedulers = new TaskScheduler<T>();
        schedulers.mTask = task;
        return schedulers;
    }

    public TaskObserve<T> subscribeOn(@Schedulers.Scheduler int scheduler) {
        this.mSubscribeScheduler = scheduler;
        return new TaskObserve<T>(new TaskEmitter<T>(mTask, mSubscribeScheduler));
    }
}
