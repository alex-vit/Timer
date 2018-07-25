package com.alexvit.timer.timer;

abstract class TimerState {

    abstract int getSeconds();

    static TimerState running(int seconds) {
        return new Running(seconds);
    }

    static TimerState stopped(int seconds) {
        return new Stopped(seconds);
    }

    static TimerState completed() {
        return new Completed();
    }

    static class Running extends TimerState {
        private final int seconds;

        private Running(int seconds) {
            this.seconds = seconds;
        }

        @Override
        int getSeconds() {
            return seconds;
        }
    }

    static class Stopped extends TimerState {
        private final int seconds;

        private Stopped(int seconds) {
            this.seconds = seconds;
        }

        @Override
        int getSeconds() {
            return seconds;
        }
    }

    static class Completed extends TimerState {
        private Completed() {
        }

        @Override
        int getSeconds() {
            return 0;
        }
    }

}
