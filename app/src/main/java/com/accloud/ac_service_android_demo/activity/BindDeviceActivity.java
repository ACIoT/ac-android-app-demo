package com.accloud.ac_service_android_demo.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.accloud.ac_service_android_demo.R;
import com.accloud.ac_service_android_demo.config.Config;
import com.accloud.ac_service_android_demo.utils.Pop;
import com.accloud.cloudservice.AC;
import com.accloud.cloudservice.PayloadCallback;
import com.accloud.service.ACException;
import com.accloud.service.ACUserDevice;
import com.accloud.utils.PreferencesUtils;

/**
 * 使用物理ID绑定设备
 * <p>
 * Created by sudongsheng on 16/6/13.
 */
public class BindDeviceActivity extends Activity implements View.OnClickListener {
    private TextView back;
    private EditText physicalDeviceIdEdt;
    private EditText nameEdt;
    private Button bind;
    private String subDomain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_device);
        back = (TextView) findViewById(R.id.back);
        physicalDeviceIdEdt = (EditText) findViewById(R.id.physicalDeviceId);
        nameEdt = (EditText) findViewById(R.id.name);
        bind = (Button) findViewById(R.id.bind);
        back.setOnClickListener(this);
        bind.setOnClickListener(this);
        subDomain = PreferencesUtils.getString(this, "subDomain", Config.SUBDOMAIN);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.bind:
                String physicalDeviceId = physicalDeviceIdEdt.getText().toString();
                String name = nameEdt.getText().toString();
                AC.bindMgr().bindDevice(subDomain, physicalDeviceId, name, new PayloadCallback<ACUserDevice>() {
                    @Override
                    public void success(ACUserDevice userDevice) {
                        Pop.popToast(BindDeviceActivity.this, getString(R.string.bind_device_aty_device_bind_success, userDevice.getDeviceId()));
                        finish();
                    }

                    @Override
                    public void error(ACException e) {
                        Pop.popToast(BindDeviceActivity.this, e.getErrorCode() + "-->" + e.getMessage());
                    }
                });
                break;
        }
    }
}
