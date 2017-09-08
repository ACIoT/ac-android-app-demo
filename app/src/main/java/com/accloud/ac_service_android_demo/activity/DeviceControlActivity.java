package com.accloud.ac_service_android_demo.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.accloud.ac_service_android_demo.R;
import com.accloud.ac_service_android_demo.constant.IntentKey;
import com.accloud.ac_service_android_demo.databinding.ActivityDeviceControlBinding;
import com.accloud.ac_service_android_demo.manager.DeviceManager;
import com.accloud.ac_service_android_demo.utils.DeviceApi;
import com.accloud.ac_service_android_demo.utils.ToastUtil;
import com.accloud.cloudservice.AC;
import com.accloud.cloudservice.VoidCallback;
import com.accloud.service.ACException;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by liuxiaofeng on 08/09/2017.
 */

public class DeviceControlActivity extends AppCompatActivity {

    private long deviceId;
    private ActivityDeviceControlBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("设备控制");
        deviceId = getIntent().getLongExtra(IntentKey.DEVICE_ID, -1);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_device_control);
        binding.setDevice(DeviceManager.getInstance().getDevice(deviceId));
        ButterKnife.bind(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_device_control_aty, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        } else if (item.getItemId() == R.id.unbind) {
            unBindDevice();
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.lightSwitchBtn)
    public void onViewClicked() {
        if (DeviceManager.getInstance().getDevice(deviceId).isPowerOn()) {
            DeviceApi.closeLight(DeviceManager.getInstance().getDevice(deviceId).getPhysicalDeviceId(), null);
        } else {
            DeviceApi.openLight(DeviceManager.getInstance().getDevice(deviceId).getPhysicalDeviceId(), null);
        }
    }

    private void unBindDevice() {
        AC.bindMgr().unbindDevice(DeviceManager.getInstance().getDevice(deviceId).getSubDomain(), deviceId, new VoidCallback() {
            @Override
            public void success() {
                setResult(RESULT_OK);
                DeviceControlActivity.this.finish();
            }

            @Override
            public void error(ACException e) {
                ToastUtil.show(DeviceControlActivity.this, e.getErrorCode() + "-->" + e.getMessage());
            }
        });
    }
}
