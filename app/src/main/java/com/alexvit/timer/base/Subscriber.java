package com.alexvit.timer.base;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

public class Subscriber implements LifecycleObserver {

    private final boolean unsubscribeOnPause;
    private final CompositeDisposable subs = new CompositeDisposable();

    public Subscriber(Lifecycle lifecycle) {
        this(lifecycle, true);
    }

    public Subscriber(Lifecycle lifecycle, boolean unsubscribeOnPause) {
        this.unsubscribeOnPause = unsubscribeOnPause;
        lifecycle.addObserver(this);
    }

    public final <A> boolean subscribe(Observable<A> observable, Consumer<A> onNext) {
        return subscribe(observable, onNext, throwable -> {
        });
    }

    public final <A> boolean subscribe(
            Observable<A> observable,
            Consumer<A> onNext,
            Consumer<Throwable> onError
    ) {
        return subscribe(observable, onNext, onError, () -> {
        });
    }

    public final <A> boolean subscribe(
            Observable<A> observable,
            Consumer<A> onNext,
            Consumer<Throwable> onError,
            Action onComplete
    ) {
        Disposable sub = observable.subscribe(onNext, onError, onComplete);
        return subs.add(sub);
    }

    public void clear() {
        subs.clear();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause() {
        if (unsubscribeOnPause) {
            clear();
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onStop() {
        if (!unsubscribeOnPause) {
            clear();
        }
    }

}
