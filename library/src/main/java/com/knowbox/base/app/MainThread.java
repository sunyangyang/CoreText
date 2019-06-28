package com.knowbox.base.app;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;

public class MainThread {

    private static volatile MainThread instance;
    private MainHandlerExecutor executor;

    private static class MainHandlerExecutor implements Executor {

        private static final Handler handler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(Runnable runnable) {
            execute(runnable, 0);
        }

        void execute(Runnable runnable, long delayMillis) {
            handler.postDelayed(runnable, delayMillis);
        }
    }

    private MainThread() {
        if (executor == null) {
            executor = new MainHandlerExecutor();
        }
    }

    public static MainThread getInstance() {
        if (instance == null) {
            synchronized (MainThread.class) {
                if (instance == null) {
                    instance = new MainThread();
                }
            }
        }
        return instance;
    }

    public void execute(Runnable runnable) {
        executor.execute(runnable);
    }

    public void execute(Runnable runnable, long delayMillis) {
        executor.execute(runnable, delayMillis);
    }
}
