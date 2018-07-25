package com.alexvit.timer.timer;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.BehaviorSubject;

import static java.util.concurrent.TimeUnit.SECONDS;

public class TimerRepository {

    static final int DEFAULT_SECONDS = 5 * 60;

    private int initialSeconds = DEFAULT_SECONDS;
    private int currentSeconds = DEFAULT_SECONDS;
    private boolean isRunning = false;

    private final BehaviorSubject<TimerState> timerState = BehaviorSubject.createDefault(
            TimerState.stopped(DEFAULT_SECONDS)
    );
    private final CompositeDisposable subs = new CompositeDisposable();

    Observable<TimerState> getTimerState() {
        return timerState.toFlowable(BackpressureStrategy.LATEST).toObservable();
    }

    void setTimer(int to) {
        stop();

        initialSeconds = to;
        currentSeconds = initialSeconds;

        start();

    }

    boolean isReset() {
        return initialSeconds == currentSeconds;
    }

    void start() {
        if (isRunning) {
            return;
        }

        isRunning = true;

        Disposable sub = countdown(currentSeconds)
                .map(this::createTimerState)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onTimerNext, t -> {
                }, this::onTimerComplete);
        subs.add(sub);
    }

    void stop() {
        if (!isRunning) {
            return;
        }

        isRunning = false;

        subs.clear();
        timerState.onNext(TimerState.stopped(currentSeconds));
    }

    void reset() {
        subs.clear();
        isRunning = false;
        currentSeconds = initialSeconds;
        timerState.onNext(TimerState.stopped(currentSeconds));
    }

    private void onTimerNext(TimerState nextState) {
        currentSeconds = nextState.getSeconds();
        timerState.onNext(nextState);
    }

    private void onTimerComplete() {
        currentSeconds = 0;
        timerState.onNext(TimerState.completed());
    }

    private Observable<Integer> countdown(int from) {
        return Observable.interval(0, 1, SECONDS)
                .map(passed -> from - passed.intValue())
                .flatMap(seconds -> {
                    if (seconds > 0) {
                        return Observable.just(seconds);
                    } else {
                        return Observable.empty();
                    }
                });
    }

    private TimerState createTimerState(int at) {
        if (at < 1) throw new IllegalArgumentException("Handle timer 0 in countdown.onComplete");

        if (isRunning) {
            return TimerState.running(at);
        } else {
            return TimerState.stopped(at);
        }
    }

}
