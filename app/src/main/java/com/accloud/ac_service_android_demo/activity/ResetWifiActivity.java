package com.accloud.ac_service_android_demo.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.accloud.ac_service_android_demo.R;
import com.accloud.ac_service_android_demo.utils.Pop;
import com.accloud.cloudservice.AC;
import com.accloud.cloudservice.ACDeviceActivator;
import com.accloud.cloudservice.PayloadCallback;
import com.accloud.service.ACDeviceBind;
import com.accloud.service.ACException;
import com.accloud.utils.PreferencesUtils;

/**
 * 重置Wifi密码
 * <p>
 * Created by sudongsheng on 2015/1/27.
 */
public class ResetWifiActivity extends Activity implements View.OnClickListener {

    private TextView back;
    private TextView wifi_name;
    private Button reset;
    private EditText password;

    ACDeviceActivator deviceActivator;
    private String physicalDeviceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        physicalDeviceId = getIntent().getStringExtra("physicalDeviceId");
        setContentView(R.layout.activity_reset_wifi);
        back = (TextView) findViewById(R.id.reset_wifi_back);
        wifi_name = (TextView) findViewById(R.id.reset_text_value);
        reset = (Button) findViewById(R.id.reset);
        password = (EditText) findViewById(R.id.reset_edit_pwd);
        back.setOnClickListener(this);
        reset.setOnClickListener(this);
        deviceActivator = AC.deviceActivator(PreferencesUtils.getInt(this, "deviceType", ACDeviceActivator.HF));
        wifi_name.setText(deviceActivator.getSSID());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.reset_wifi_back:
                finish();
                break;
            case R.id.reset:
                reset.setEnabled(false);
                reset.setText(R.string.reset_wifi_aty_reset_ing);
                deviceActive();
                break;
        }
    }

    public void deviceActive() {
        deviceActivator.startAbleLink(deviceActivator.getSSID(), password.getText().toString(), physicalDeviceId, AC.DEVICE_ACTIVATOR_DEFAULT_TIMEOUT, new PayloadCallback<ACDeviceBind>() {
            @Override
            public void success(ACDeviceBind deviceBind) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        reset.setEnabled(true);
                        reset.setText(R.string.reset_wifi_aty_reset_device_wifi);
                        Pop.popToast(ResetWifiActivity.this, getString(R.string.reset_wifi_aty_reset_device_wifi_success));
                    }
                });
            }

            @Override
            public void error(final ACException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        reset.setEnabled(true);
                        reset.setText(R.string.reset_wifi_aty_reset_device_wifi);
                        Pop.popToast(ResetWifiActivity.this, e.getErrorCode() + "-->" + e.getMessage());
                    }
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        deviceActivator.stopAbleLink();
    }
}
