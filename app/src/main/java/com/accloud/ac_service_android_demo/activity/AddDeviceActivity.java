package com.accloud.ac_service_android_demo.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.accloud.ac_service_android_demo.R;
import com.accloud.ac_service_android_demo.config.Config;
import com.accloud.ac_service_android_demo.utils.Pop;
import com.accloud.ac_service_android_demo.utils.ViewHolder;
import com.accloud.cloudservice.AC;
import com.accloud.cloudservice.ACDeviceActivator;
import com.accloud.cloudservice.PayloadCallback;
import com.accloud.service.ACBindMgr;
import com.accloud.service.ACDeviceBind;
import com.accloud.service.ACException;
import com.accloud.service.ACUserDevice;
import com.accloud.utils.PreferencesUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 通过smartLink配网并绑定设备
 */
public class AddDeviceActivity extends Activity implements View.OnClickListener {

    private TextView back;
    private TextView wifi_name;
    private TextView bindDevice;
    private TextView apTV;
    private Button connect;
    private EditText password;

    private String subDomain;

    MyAdapter mAdapter;
    AlertDialog.Builder builder;

    ACDeviceActivator deviceActivator;
    ACBindMgr bindMgr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_device);
        back = (TextView) findViewById(R.id.add_device_back);
        wifi_name = (TextView) findViewById(R.id.add_text_value);
        bindDevice = (TextView) findViewById(R.id.bind);
        apTV = (TextView) findViewById(R.id.ap);
        connect = (Button) findViewById(R.id.connect);
        password = (EditText) findViewById(R.id.add_edit_pwd);
        bindDevice.setOnClickListener(this);
        apTV.setOnClickListener(this);
        back.setOnClickListener(this);
        connect.setOnClickListener(this);

        subDomain = PreferencesUtils.getString(this, "subDomain", Config.SUBDOMAIN);
        deviceActivator = AC.deviceActivator(PreferencesUtils.getInt(this, "deviceType", AC.DEVICE_HF));
        bindMgr = AC.bindMgr();
    }

    @Override
    protected void onResume() {
        super.onResume();
        wifi_name.setText(deviceActivator.getSSID());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_device_back:
                finish();
                break;
            case R.id.connect:
                connect.setEnabled(false);
                connect.setText(R.string.add_device_aty_device_activate_ing);
                deviceActive();
                break;
            case R.id.bind:
                startActivity(new Intent(AddDeviceActivity.this, BindDeviceActivity.class));
                finish();
                break;
            case R.id.ap:
                startActivity(new Intent(AddDeviceActivity.this, APDeviceActivity.class));
                finish();
                break;
        }
    }

    public void deviceActive() {
        deviceActivator.startAbleLink(deviceActivator.getSSID(), password.getText().toString(), AC.DEVICE_ACTIVATOR_DEFAULT_TIMEOUT, new PayloadCallback<List<ACDeviceBind>>() {
            @Override
            public void success(List<ACDeviceBind> deviceBinds) {
                //此处每激活一个设备会callback一次

                //我们通过弹框，没激活成功一台设备显示一台设备。若只想绑定第一个配网成功的设备，则可以在此调stopAbleLink，同时取列表里的第一个设备直接进行绑定即可。
                showChosenDialog(deviceBinds);
            }

            @Override
            public void error(ACException e) {
                connect.setEnabled(true);
                connect.setText(R.string.add_device_aty_re_activate);
                Pop.popToast(AddDeviceActivity.this, e.getErrorCode() + "-->" + e.getMessage());
            }
        });
    }

    public void showChosenDialog(List<ACDeviceBind> deviceBinds) {
        if (builder == null) {
            builder = new AlertDialog.Builder(this);
            mAdapter = new MyAdapter(this);
            builder.setTitle(R.string.add_device_aty_choose_device_to_bind)
                    .setAdapter(mAdapter, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if ((boolean) mAdapter.getItem(which).getTag()) {
                                //若该设备已被绑定
                                Pop.popToast(AddDeviceActivity.this, getString(R.string.add_device_aty_device_is_bounded));
                            } else {
                                dialog.dismiss();
                                showNameDialog(mAdapter.getItem(which).getPhysicalDeviceId());
                            }
                        }
                    })
                    .setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            builder = null;
                            connect.setEnabled(true);
                            connect.setText(R.string.add_device_aty_re_activate);
                            //停止激活设备
                            deviceActivator.stopAbleLink();
                        }
                    }).show();
        }
        for (final ACDeviceBind deviceBind : deviceBinds) {
            if (deviceBind.getTag() == null) {
                bindMgr.isDeviceBound(subDomain, deviceBind.getPhysicalDeviceId(), new PayloadCallback<Boolean>() {
                    @Override
                    public void success(Boolean isBound) {
                        deviceBind.setTag(isBound);
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void error(ACException e) {
                    }
                });
            }
        }
        mAdapter.deviceList = deviceBinds;
        mAdapter.notifyDataSetChanged();
    }

    public void showNameDialog(final String physicalDeviceId) {
        final EditText editName = new EditText(this);
        new AlertDialog.Builder(this).setTitle(R.string.add_deviec_aty_input_devicename_to_bind)
                .setView(editName).setPositiveButton(getString(R.string.add_device_aty_confirmbtn_text), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                bindDevice(physicalDeviceId, editName.getText().toString());
            }
        }).show();
    }

    public void bindDevice(String physicalDeviceId, String deviceName) {
        bindMgr.bindDevice(subDomain, physicalDeviceId, deviceName, new PayloadCallback<ACUserDevice>() {
            @Override
            public void success(ACUserDevice userDevice) {
                Pop.popToast(AddDeviceActivity.this, getString(R.string.add_device_aty_device_bind_success, userDevice.getDeviceId()));
                AddDeviceActivity.this.finish();
            }

            @Override
            public void error(ACException e) {
                Pop.popToast(AddDeviceActivity.this, e.getErrorCode() + "-->" + e.getMessage());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (deviceActivator.isAbleLink())
            deviceActivator.stopAbleLink();
    }

    class MyAdapter extends BaseAdapter {
        public List<ACDeviceBind> deviceList;
        private Context context;

        public MyAdapter(Context context) {
            this.context = context;
            deviceList = new ArrayList<>();
        }

        @Override
        public int getCount() {
            return deviceList.size();
        }

        @Override
        public ACDeviceBind getItem(int i) {
            return deviceList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            view = LayoutInflater.from(context).inflate(R.layout.adapter_add_device, null);
            TextView physicalDeviceIdTV = ViewHolder.get(view, R.id.physicalDeviceId);
            final TextView boundStatusTV = ViewHolder.get(view, R.id.boundStatus);

            final ACDeviceBind device = deviceList.get(i);
            physicalDeviceIdTV.setText(device.getPhysicalDeviceId());
            if (device.getTag() == null) {
                boundStatusTV.setText(R.string.add_device_aty_bind_status_unknown);
            } else {
                boundStatusTV.setText(((boolean) device.getTag()) ? R.string.add_device_aty_bind_status_bind : R.string.add_device_aty_bind_status_unbind);
            }
            return view;
        }
    }
}
