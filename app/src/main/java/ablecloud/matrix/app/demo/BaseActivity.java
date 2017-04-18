package ablecloud.matrix.app.demo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import javax.inject.Inject;

import ablecloud.matrix.app.DeviceManager;

/**
 * Created by wangkun on 18/04/2017.
 */

public class BaseActivity extends AppCompatActivity {
    @Inject
    DeviceManager deviceManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DaggerActivityComponent.builder()
                .applicationComponent(((MainApplication) getApplication()).getApplicationComponent())
                .build()
                .inject(this);
    }
}
