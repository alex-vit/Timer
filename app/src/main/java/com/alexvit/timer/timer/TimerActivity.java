package com.alexvit.timer.timer;

import android.app.AlertDialog;
import android.arch.lifecycle.Lifecycle;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.NumberPicker;

import com.alexvit.timer.R;
import com.alexvit.timer.base.BaseActivity;
import com.alexvit.timer.base.Subscriber;
import com.lb.auto_fit_textview.AutoResizeTextView;

import java.util.Locale;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class TimerActivity extends BaseActivity<TimerViewModel> {

    private AutoResizeTextView timer;
    private Button startStop;
    private Button reset;

    private Subscriber subscriber;

    private Ringtone sound;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        subscriber = new Subscriber(getLifecycle());
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_timer;
    }

    @Override
    protected TimerViewModel createViewModel(Lifecycle lifecycle) {
        return new TimerViewModel(lifecycle);
    }

    @Override
    protected void bindViews() {
        timer = findViewById(R.id.timer);
        timer.setOnClickListener(__ -> showMinutesInputDialog());
        startStop = findViewById(R.id.start_stop);
        reset = findViewById(R.id.reset);
    }

    @Override
    protected void bindViewModel(TimerViewModel viewModel) {
        subscriber.subscribe(viewModel.uiState(), this::onUiState);

        startStop.setOnClickListener(__ -> viewModel.start());
        reset.setOnClickListener(__ -> viewModel.reset());
    }

    private void onUiState(TimerUiState state) {
        boolean isRunning = state.timerState instanceof TimerState.Running;
        setKeepScreenOn(isRunning);
        updateService(isRunning);

        int seconds = state.timerState.getSeconds();
        showSeconds(seconds);

        setResetVisibility(state.showReset);
        updateStartStop(state.showStart);

        boolean completed = state.timerState instanceof TimerState.Completed;
        if (completed) playSound();
        else stopSound();
    }

    private void showSeconds(int currentSeconds) {
        int minutes = currentSeconds / 60;
        int seconds = currentSeconds % 60;
        String timerString = String.format(Locale.getDefault(), "%d:%02d", minutes, seconds);
        timer.setText(timerString);
    }

    private void setResetVisibility(boolean visible) {
        int visibility = (visible) ? VISIBLE : GONE;
        reset.setVisibility(visibility);
    }

    private void updateStartStop(boolean isStart) {
        int color = (isStart) ? R.color.green : R.color.yellow;
        int text = (isStart) ? R.string.start : R.string.stop;
        View.OnClickListener onClick = (isStart)
                ? __ -> viewModel.start()
                : __ -> viewModel.stop();

        startStop.setBackgroundColor(ContextCompat.getColor(this, color));
        startStop.setText(text);
        startStop.setOnClickListener(onClick);
    }

    private void setKeepScreenOn(boolean on) {
        int flag = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        if (on) {
            getWindow().addFlags(flag);
        } else {
            getWindow().clearFlags(flag);
        }
    }

    private void playSound() {
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        sound = RingtoneManager.getRingtone(getApplicationContext(), notification);
        sound.play();
    }

    private void stopSound() {
        if (sound != null) {
            sound.stop();
            sound = null;
        }
    }

    private void showMinutesInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_set_timer, null);
        builder.setTitle(R.string.dialog_set_timer)
                .setView(view);

        NumberPicker picker = view.findViewById(R.id.picker);
        Button set = view.findViewById(R.id.set);

        picker.setMinValue(1);
        picker.setMaxValue(99);
        picker.setValue(TimerRepository.DEFAULT_SECONDS);

        AlertDialog dialog = builder.create();

        set.setOnClickListener(__ -> {
            viewModel.setTimer(60 * picker.getValue());
            dialog.dismiss();
        });

        dialog.show();
    }

    private void updateService(boolean isRunning) {
        if (isRunning) {
            TimerForegroundService.start(this);
        } else {
            TimerForegroundService.stop(this);
        }
    }

}
