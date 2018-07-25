package com.alexvit.timer;

import android.app.Application;
import android.support.annotation.GuardedBy;
import android.support.annotation.NonNull;

import com.alexvit.timer.timer.TimerRepository;

public class App extends Application {

    private static App INSTANCE;

    private final Object timerLock = new Object();
    @GuardedBy("timerLock")
    private TimerRepository timer;

    @Override
    public void onCreate() {
        super.onCreate();

        INSTANCE = this;
    }

    @NonNull
    public static App getInstance() {
        return INSTANCE;
    }

    @NonNull
    public static TimerRepository getTimer() {
        return App.getInstance().getTimer_();
    }

    private TimerRepository getTimer_() {
        if (timer == null) {
            synchronized (timerLock) {
                if (timer == null) {
                    timer = new TimerRepository();
                }
            }
        }
        return timer;
    }

}

