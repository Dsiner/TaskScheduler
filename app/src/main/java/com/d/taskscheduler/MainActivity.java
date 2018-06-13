package com.d.taskscheduler;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.d.lib.taskscheduler.TaskScheduler;
import com.d.lib.taskscheduler.callback.Function;
import com.d.lib.taskscheduler.callback.Observer;
import com.d.lib.taskscheduler.callback.Task;
import com.d.lib.taskscheduler.schedule.Schedulers;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private final static boolean DEBUG = false;
    private final static String TAG = "scheduler--> ";

    private TextView tvConsole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initClick();
    }

    private void initClick() {
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.tv_main:
                        tvConsole.setText("----- Start Main -----\n");
                        startMain();
                        break;
                    case R.id.tv_single:
                        tvConsole.setText("----- Start Single -----\n");
                        startSingle();
                        break;
                    case R.id.tv_task:
                        tvConsole.setText("----- Start Task -----\n");
                        startTask();
                        break;
                    case R.id.tv_create:
                        tvConsole.setText("----- Start Create -----\n");
                        startCreate();
                        break;
                }
            }
        };
        tvConsole = (TextView) findViewById(R.id.tv_console);
        findViewById(R.id.tv_main).setOnClickListener(onClickListener);
        findViewById(R.id.tv_single).setOnClickListener(onClickListener);
        findViewById(R.id.tv_task).setOnClickListener(onClickListener);
        findViewById(R.id.tv_create).setOnClickListener(onClickListener);
    }

    private void startMain() {
        TaskScheduler.executeMain(new Runnable() {
            @Override
            public void run() {
                printThread("executeMain:");
            }
        });
    }

    private void startTask() {
        TaskScheduler.executeTask(new Runnable() {
            @Override
            public void run() {
                printThread("executeTask:");
            }
        });
    }

    private void startSingle() {
        TaskScheduler.executeSingle(new Runnable() {
            @Override
            public void run() {
                printThread("executeSingle:");
            }
        });
    }

    private void startCreate() {
        TaskScheduler.create(new Task<List<String>>() {
            @Override
            public List<String> run() {
                printThread("task-0:");
                return new ArrayList<>();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .map(new Function<List<String>, String>() {
                    @Override
                    public String apply(@NonNull List<String> strings) throws Exception {
                        printThread("task-1:");
                        return "";
                    }
                })
                .observeOn(Schedulers.io())
                .map(new Function<String, Boolean>() {
                    @Override
                    public Boolean apply(@NonNull String s) throws Exception {
                        printThread("task-2:");
                        return true;
                    }
                })
                .observeOn(Schedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onNext(@NonNull Boolean result) {
                        printThread("task-n:");
                        Toast.makeText(getApplicationContext(), "onNext", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        printThread("task_e:");
                        Toast.makeText(getApplicationContext(), "onError", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Print current thread
     */
    public void printThread(String tag) {
        final String print = tag + " " + Thread.currentThread().getId() + "--NAME--" + Thread.currentThread().getName();
        if (DEBUG) {
            Log.d("Thread", TAG + print);
        } else {
            TaskScheduler.executeMain(new Runnable() {
                @Override
                public void run() {
                    tvConsole.append("--> " + print);
                    tvConsole.append("\n");
                }
            });
        }
    }
}
