package com.d.lib.taskscheduler.schedule;

import android.os.Handler;
import android.os.Looper;

import com.d.lib.taskscheduler.TaskManager;

/**
 * Schedulers
 * Created by D on 2018/5/15.
 */
public enum Schedulers {
    DEFAULT_THREAD, NEW_THREAD, IO, MAIN_THREAD;

    public static Schedulers newThread() {
        return Schedulers.NEW_THREAD;
    }

    public static Schedulers io() {
        return Schedulers.IO;
    }

    public static Schedulers mainThread() {
        return Schedulers.MAIN_THREAD;
    }

    /**
     * Switch thread
     */
    public static void switchThread(Schedulers scheduler, final Runnable runnable) {
        if (scheduler == Schedulers.NEW_THREAD) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (runnable != null) {
                        runnable.run();
                    }
                }
            }).start();
            return;
        } else if (scheduler == Schedulers.IO) {
            TaskManager.getIns().executeTask(new Runnable() {
                @Override
                public void run() {
                    if (runnable != null) {
                        runnable.run();
                    }
                }
            });
            return;
        } else if (scheduler == Schedulers.MAIN_THREAD) {
            if (!isMainThread()) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if (runnable != null) {
                            runnable.run();
                        }
                    }
                });
                return;
            }
        }
        if (runnable != null) {
            runnable.run();
        }
    }

    private static boolean isMainThread() {
        return Looper.getMainLooper().getThread() == Thread.currentThread();
    }
}
