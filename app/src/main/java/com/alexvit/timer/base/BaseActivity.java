package com.alexvit.timer.base;

import android.arch.lifecycle.Lifecycle;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

public abstract class BaseActivity<ViewModel extends BaseViewModel> extends AppCompatActivity {

    protected ViewModel viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());

        bindViews();

        viewModel = createViewModel(getLifecycle());
    }

    @Override
    protected void onResume() {
        super.onResume();

        bindViewModel(viewModel);
    }

    protected abstract @LayoutRes
    int getLayoutId();

    protected abstract ViewModel createViewModel(Lifecycle lifecycle);

    protected abstract void bindViews();

    protected abstract void bindViewModel(ViewModel viewModel);

}
