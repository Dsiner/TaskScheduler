package com.d.lib.taskscheduler.schedule;

import com.d.lib.taskscheduler.callback.Function;

/**
 * FunctionEmitter
 * Created by D on 2018/5/16.
 */
public class FunctionEmitter<T, R> extends Emitter {
    public FunctionEmitter functionEmitter;
    public Function<? super T, ? extends R> function;

    public FunctionEmitter(Function<? super T, ? extends R> function, Schedulers scheduler) {
        this.function = function;
        this.scheduler = scheduler;
    }
}
