package com.d.taskscheduler;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.d.lib.taskscheduler.TaskScheduler;
import com.d.lib.taskscheduler.callback.Function;
import com.d.lib.taskscheduler.callback.Observer;
import com.d.lib.taskscheduler.callback.Task;
import com.d.lib.taskscheduler.schedule.Schedulers;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.tv_test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTask();
            }
        });
    }

    /**
     * Just to test
     */
    private void startTask() {
        TaskScheduler.create(new Task<List<String>>() {
            @Override
            public List<String> run() {
                printThread("scheduler--> task_0");
                return new ArrayList<>();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .map(new Function<List<String>, String>() {
                    @Override
                    public String apply(@NonNull List<String> strings) throws Exception {
                        printThread("scheduler--> task_1");
                        return "";
                    }
                })
                .observeOn(Schedulers.io())
                .map(new Function<String, Boolean>() {
                    @Override
                    public Boolean apply(@NonNull String s) throws Exception {
                        printThread("scheduler--> task_2");
                        return true;
                    }
                })
                .observeOn(Schedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onNext(@NonNull Boolean result) {
                        printThread("scheduler--> task_N:");
                        Toast.makeText(getApplicationContext(), "onNext", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        printThread("scheduler--> task_E:");
                        Toast.makeText(getApplicationContext(), "onError", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Print current thread
     */
    public static void printThread(String tag) {
        Log.d("Thread", tag + " " + Thread.currentThread().getId() + "--NAME--" + Thread.currentThread().getName());
    }
}
