package com.accloud.ac_service_android_demo.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.accloud.ac_service_android_demo.R;
import com.accloud.ac_service_android_demo.config.Config;
import com.accloud.ac_service_android_demo.databinding.ActivityDeviceListBinding;
import com.accloud.ac_service_android_demo.databinding.ItemviewDeviceListBinding;
import com.accloud.ac_service_android_demo.model.Device;
import com.accloud.ac_service_android_demo.utils.DeviceApi;
import com.accloud.ac_service_android_demo.utils.ToastUtil;
import com.accloud.cloudservice.AC;
import com.accloud.cloudservice.PayloadCallback;
import com.accloud.cloudservice.VoidCallback;
import com.accloud.service.ACDeviceDataMgr;
import com.accloud.service.ACException;
import com.accloud.service.ACUserDevice;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ablecloud.support.databinding.BindingHolder;
import ablecloud.support.databinding.CountObservable;
import ablecloud.support.widget.ArrayRecyclerAdapter;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrUIHandler;
import in.srain.cube.views.ptr.indicator.PtrIndicator;

/**
 * Created by liuxiaofeng on 01/09/2017.
 */

public class DeviceListActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_BIND_DEVICE = 100;
    private DeviceListAdapter adapter;
    private ActivityDeviceListBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("设备列表");

        adapter = new DeviceListAdapter();
        binding = DataBindingUtil.setContentView(this, R.layout.activity_device_list);
        binding.setCountObservable(CountObservable.create(adapter));
        binding.deviceList.getRefreshView().setAdapter(adapter);
        binding.deviceList.addPtrUIHandler(ptrUIHandler);

        AC.deviceDataMgr().registerPropertyReceiver(propertyReceiver);
        AC.deviceDataMgr().registerOnlineStatusListener(onlineStatusListener);
        fetchDeviceList();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AC.deviceDataMgr().unregisterPropertyReceiver(propertyReceiver);
        AC.deviceDataMgr().unregisterOnlineStatusListener(onlineStatusListener);
        unSubScribeProperty();
        unSubScribeOnlineStatus();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_CODE_BIND_DEVICE) {
            fetchDeviceList();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_device_list_aty, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.add_device) {
            startActivityForResult(new Intent(this, AddDeviceActivity.class), REQUEST_CODE_BIND_DEVICE);
        } else if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        } else if (item.getItemId() == R.id.sign_out) {
            signOut();
        }
        return super.onOptionsItemSelected(item);
    }

    private void signOut() {
        AC.accountMgr().logout();
        startActivity(new Intent(this, SignInActivity.class));
        finish();
    }

    private ACDeviceDataMgr.PropertyReceiver propertyReceiver = new ACDeviceDataMgr.PropertyReceiver() {
        @Override
        public void onPropertyReceive(String subDomain, long deviceId, String property) {
            for (Device device : adapter.getItems()) {
                if (device.getDeviceId() == deviceId) {
                    try {
                        int lightStatus = new JSONObject(property).getInt("switch");
                        device.setLightStatus(lightStatus);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    };

    private ACDeviceDataMgr.OnlineStatusListener onlineStatusListener = new ACDeviceDataMgr.OnlineStatusListener() {
        @Override
        public void onStatusChanged(String subDomain, long deviceId, boolean online) {
            for (Device device : adapter.getItems()) {
                if (device.getDeviceId() == deviceId) {
                    device.setOnlineStatus(online ? ACUserDevice.NETWORK_ONLINE : ACUserDevice.OFFLINE);
                }
            }
        }
    };

    private PtrUIHandler ptrUIHandler = new PtrUIHandler() {
        @Override
        public void onUIReset(PtrFrameLayout frame) {

        }

        @Override
        public void onUIRefreshPrepare(PtrFrameLayout frame) {

        }

        @Override
        public void onUIRefreshBegin(PtrFrameLayout frame) {
            fetchDeviceList();
        }

        @Override
        public void onUIRefreshComplete(PtrFrameLayout frame) {

        }

        @Override
        public void onUIPositionChange(PtrFrameLayout frame, boolean isUnderTouch, byte status, PtrIndicator ptrIndicator) {

        }
    };

    public void fetchDeviceList() {
        AC.bindMgr().listDevicesWithStatus(new PayloadCallback<List<ACUserDevice>>() {
            @Override
            public void success(List<ACUserDevice> deviceList) {
                ArrayList<Device> devices = toDevices(deviceList);
                subScribeProperty(devices);
                subScribeOnlineStatus(devices);
                adapter.clear();
                adapter.addAll(devices);
            }

            @Override
            public void error(ACException e) {
            }
        });
    }

    private ArrayList<Device> toDevices(List<ACUserDevice> deviceList) {
        ArrayList<Device> devices = new ArrayList<>();
        for (ACUserDevice acUserDevice : deviceList) {
            Device device = new Device(acUserDevice.getDeviceId(), acUserDevice.getPhysicalDeviceId(), acUserDevice.getStatus(), 0);
            devices.add(device);
        }
        return devices;
    }

    private void subScribeProperty(ArrayList<Device> devices) {
        for (Device device : devices) {
            AC.deviceDataMgr().subscribeProperty(Config.SUB_DOMAIN, device.getDeviceId(), null);
        }
    }

    private void subScribeOnlineStatus(ArrayList<Device> devices) {
        for (Device device : devices) {
            AC.deviceDataMgr().subscribeOnlineStatus(Config.SUB_DOMAIN, device.getDeviceId(), null);
        }
    }

    private void unSubScribeProperty() {
        AC.deviceDataMgr().unSubscribeAllProperty();
    }

    private void unSubScribeOnlineStatus() {
        AC.deviceDataMgr().unSubscribeAllOnlineStatus();
    }


    private class DeviceListAdapter extends ArrayRecyclerAdapter<Device, BindingHolder> {

        @Override
        public BindingHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            return new BindingHolder(DataBindingUtil.inflate(inflater, R.layout.itemview_device_list, parent, false));
        }

        @Override
        public void onBindViewHolder(BindingHolder holder, int position) {
            final Device device = getItem(position);
            ItemviewDeviceListBinding binding = (ItemviewDeviceListBinding) holder.binding;
            binding.setDevice(device);
            setListener(device, binding);
        }

        public List<Device> getItems() {
            return mItems;
        }
    }

    private void setListener(final Device device, ItemviewDeviceListBinding binding) {
        binding.getRoot().setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new AlertDialog.Builder(v.getContext()).setTitle(v.getContext().getString(R.string.main_aty_delete_device_title)).setMessage(getString(R.string.main_aty_delete_device_desc))
                        .setNegativeButton(v.getContext().getString(R.string.main_aty_delete_device_cancle), null)
                        .setPositiveButton(v.getContext().getString(R.string.main_aty_delete_device_confirm), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int m) {
                                unbindDevice(device);
                            }
                        }).show();
                return false;
            }
        });
        binding.openLight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeviceApi.openLight(device.getPhysicalDeviceId(), null);
            }
        });
        binding.closeLight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeviceApi.closeLight(device.getPhysicalDeviceId(), null);
            }
        });
    }

    public void unbindDevice(Device device) {
        AC.bindMgr().unbindDevice(Config.SUB_DOMAIN, device.getDeviceId(), new VoidCallback() {
            @Override
            public void success() {
                ToastUtil.show(DeviceListActivity.this, getString(R.string.main_aty_delete_device_success));
                fetchDeviceList();
            }

            @Override
            public void error(ACException e) {
                ToastUtil.show(DeviceListActivity.this, e.getErrorCode() + "-->" + e.getMessage());
            }
        });
    }
}
