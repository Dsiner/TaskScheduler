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
    private static final boolean DEBUG = false;
    private static final String TAG = "scheduler--> ";

    private TextView tv_console;

    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_main:
                    tv_console.setText("----- Start Main -----\n");
                    startMain();
                    break;

                case R.id.tv_single:
                    tv_console.setText("----- Start Single -----\n");
                    startSingle();
                    break;

                case R.id.tv_task:
                    tv_console.setText("----- Start Task -----\n");
                    startTask();
                    break;

                case R.id.tv_create:
                    tv_console.setText("----- Start Create -----\n");
                    startCreate();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindView();
    }

    private void bindView() {
        tv_console = (TextView) findViewById(R.id.tv_console);

        findViewById(R.id.tv_main).setOnClickListener(mOnClickListener);
        findViewById(R.id.tv_single).setOnClickListener(mOnClickListener);
        findViewById(R.id.tv_task).setOnClickListener(mOnClickListener);
        findViewById(R.id.tv_create).setOnClickListener(mOnClickListener);
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
        final String print = tag + " "
                + Thread.currentThread().getId()
                + "--NAME--" + Thread.currentThread().getName();
        if (DEBUG) {
            Log.d("Thread", TAG + print);
        } else {
            TaskScheduler.executeMain(new Runnable() {
                @Override
                public void run() {
                    tv_console.append("--> " + print);
                    tv_console.append("\n");
                }
            });
        }
    }
}
