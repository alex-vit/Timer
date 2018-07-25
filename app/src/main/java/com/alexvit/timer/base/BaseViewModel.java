package com.alexvit.timer.base;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;

public abstract class BaseViewModel implements LifecycleObserver {

    private final Lifecycle lifecycle;

    public BaseViewModel(Lifecycle lifecycle) {
        this.lifecycle = lifecycle;
        lifecycle.addObserver(this);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    void onStop() {
    }
}
