package com.alexvit.timer.timer;

class TimerUiState {

    final TimerState timerState;
    final boolean showStart;
    final boolean showReset;

    TimerUiState(TimerState timerState, boolean showStart, boolean showReset) {
        this.timerState = timerState;
        this.showStart = showStart;
        this.showReset = showReset;
    }
}
