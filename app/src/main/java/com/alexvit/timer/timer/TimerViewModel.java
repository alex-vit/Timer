package com.alexvit.timer.timer;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.OnLifecycleEvent;

import com.alexvit.timer.App;
import com.alexvit.timer.base.BaseViewModel;
import com.alexvit.timer.base.Subscriber;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;

class TimerViewModel extends BaseViewModel {

    private final Subscriber subscriber;
    private final TimerRepository timer = App.getTimer();

    private final BehaviorSubject<TimerUiState> uiStateSubject = BehaviorSubject.create();

    TimerViewModel(Lifecycle lifecycle) {
        super(lifecycle);

        subscriber = new Subscriber(lifecycle, false);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {
        subscriber.subscribe(timer.getTimerState(), this::onTimerState);
    }

    Observable<TimerUiState> uiState() {
        return uiStateSubject.toFlowable(BackpressureStrategy.LATEST).toObservable();
    }

    void start() {
        timer.start();
    }

    void stop() {
        timer.stop();
    }

    void reset() {
        timer.reset();
    }

    void setTimer(int to) {
        timer.setTimer(to);
    }

    private void onTimerState(TimerState timerState) {
        boolean showStart;
        boolean showReset;

        if (timerState instanceof TimerState.Running) {
            showStart = false;
            showReset = true;
        } else {
            showStart = true;
            showReset = !timer.isReset();
        }

        TimerUiState uiState = new TimerUiState(timerState, showStart, showReset);
        uiStateSubject.onNext(uiState);
    }

}
