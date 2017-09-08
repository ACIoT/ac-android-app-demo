package com.accloud.ac_service_android_demo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.accloud.ac_service_android_demo.R;
import com.accloud.cloudservice.AC;

/**
 * Created by liuxiaofeng on 05/09/2017.
 */

public class WelcomeActivity extends AppCompatActivity {

    public static final int SPLASH_DURATION = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_welcome);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!AC.accountMgr().isLogin()) {
                    startActivity(new Intent(WelcomeActivity.this, SignInActivity.class));
                } else {
                    startActivity(new Intent(WelcomeActivity.this, DeviceListActivity.class));
                }
                finish();
            }
        }, SPLASH_DURATION);
    }
}
