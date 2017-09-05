package com.accloud.ac_service_android_demo.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.accloud.ac_service_android_demo.R;
import com.accloud.ac_service_android_demo.fragment.InputAccountFragment;
import com.accloud.ac_service_android_demo.utils.FragmentUtil;

/**
 * Created by liuxiaofeng on 05/09/2017.
 */

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getSupportActionBar().setTitle("注册");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        InputAccountFragment inputAccountFragment = new InputAccountFragment();
        FragmentUtil.replaceSupportFragment(this, R.id.container, inputAccountFragment, InputAccountFragment.TAG, false, false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
