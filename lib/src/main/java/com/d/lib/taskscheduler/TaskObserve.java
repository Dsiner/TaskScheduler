package com.d.lib.taskscheduler;

import com.d.lib.taskscheduler.callback.Function;
import com.d.lib.taskscheduler.callback.Observer;
import com.d.lib.taskscheduler.schedule.FunctionEmitter;
import com.d.lib.taskscheduler.schedule.Schedulers;
import com.d.lib.taskscheduler.schedule.TaskEmitter;

import java.util.ArrayList;
import java.util.List;

public class TaskObserve<T> {
    private TaskEmitter mTaskEmitter;
    private List<FunctionEmitter> mEmitters;
    private int mObserveOnScheduler = Schedulers.defaultThread();

    private TaskObserve() {
    }

    TaskObserve(TaskEmitter<T> taskEmitter) {
        this.mTaskEmitter = taskEmitter;
        this.mEmitters = new ArrayList<>();
    }

    TaskObserve(TaskObserve middle) {
        this.mTaskEmitter = middle.mTaskEmitter;
        this.mObserveOnScheduler = middle.mObserveOnScheduler;
        this.mEmitters = middle.mEmitters;
    }

    public TaskObserve<T> observeOn(@Schedulers.Scheduler int scheduler) {
        this.mObserveOnScheduler = scheduler;
        return this;
    }

    public <TR> TaskObserve<TR> map(Function<? super T, ? extends TR> f) {
        this.mEmitters.add(new FunctionEmitter<T, TR>(f, mObserveOnScheduler));
        return new TaskObserve<TR>(this);
    }

    public void subscribe() {
        subscribe(null);
    }

    public void subscribe(final Observer<T> callback) {
        Schedulers.switchThread(mTaskEmitter.scheduler, new Runnable() {
            @Override
            public void run() {
                try {
                    Object t = mTaskEmitter.task.run();
                    if (assertInterrupt(t)) {
                        submit(t, callback);
                        return;
                    }
                    apply(t, mEmitters, callback);
                } catch (Throwable e) {
                    error(e, callback);
                }
            }
        });
    }

    private <E, F> void apply(final E o, final List<FunctionEmitter> emitters, final Observer<F> callback) {
        final FunctionEmitter<E, F> f = emitters.get(0);
        emitters.remove(f);
        Schedulers.switchThread(f.scheduler, new Runnable() {
            @Override
            public void run() {
                try {
                    Object emitter = f.function.apply(o);
                    if (assertInterrupt(emitter)) {
                        submit(emitter, callback);
                        return;
                    }
                    apply(emitter, emitters, callback);
                } catch (Throwable e) {
                    error(e, callback);
                }
            }
        });
    }

    private boolean assertInterrupt(Object emitter) throws Exception {
        if (emitter == null) {
            throw new RuntimeException("Apply output must not be null!");
        }
        return mEmitters.size() <= 0;
    }

    private <S> void submit(final Object result, final Observer<S> callback) {
        Schedulers.switchThread(mObserveOnScheduler, new Runnable() {
            @Override
            public void run() {
                try {
                    if (callback != null) {
                        callback.onNext((S) result);
                    }
                } catch (Throwable e) {
                    error(e, callback);
                }
            }
        });
    }

    private <S> void error(final Throwable e, final Observer<S> callback) {
        Schedulers.switchThread(mObserveOnScheduler, new Runnable() {
            @Override
            public void run() {
                if (callback != null) {
                    callback.onError(e);
                }
            }
        });
    }
}
