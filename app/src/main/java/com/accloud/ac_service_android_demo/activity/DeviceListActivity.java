package com.accloud.ac_service_android_demo.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.accloud.ac_service_android_demo.R;
import com.accloud.ac_service_android_demo.config.Config;
import com.accloud.ac_service_android_demo.constant.IntentKey;
import com.accloud.ac_service_android_demo.databinding.ActivityDeviceListBinding;
import com.accloud.ac_service_android_demo.databinding.ItemviewDeviceListBinding;
import com.accloud.ac_service_android_demo.manager.DeviceManager;
import com.accloud.ac_service_android_demo.model.Device;
import com.accloud.ac_service_android_demo.utils.DeviceApi;
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

import ablecloud.support.databinding.CountObservable;

/**
 * Created by liuxiaofeng on 01/09/2017.
 */

public class DeviceListActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_ADD_DEVICE = 100;
    private static final int REQUEST_CODE_CONTROL_DEVICE = 100;
    private DeviceListAdapter adapter;
    private ActivityDeviceListBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("设备列表");

        adapter = new DeviceListAdapter();
        binding = DataBindingUtil.setContentView(this, R.layout.activity_device_list);
        binding.setCountObservable(CountObservable.create(adapter));
        binding.deviceList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        binding.deviceList.setAdapter(adapter);
        binding.swipeRefreshLayout.setOnRefreshListener(onRefreshListener);

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
        if (requestCode == REQUEST_CODE_ADD_DEVICE || requestCode == REQUEST_CODE_CONTROL_DEVICE) {
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
            startActivityForResult(new Intent(this, AddDeviceActivity.class), REQUEST_CODE_ADD_DEVICE);
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
                        device.setPowerOn(lightStatus == 1);
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
                    device.setOnline(online);
                }
            }
        }
    };

    private SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            fetchDeviceList();
        }
    };

    public void fetchDeviceList() {
        if (!binding.swipeRefreshLayout.isRefreshing()) {
            binding.swipeRefreshLayout.setRefreshing(true);
        }
        AC.bindMgr().listDevicesWithStatus(new PayloadCallback<List<ACUserDevice>>() {
            @Override
            public void success(List<ACUserDevice> deviceList) {
                binding.swipeRefreshLayout.setRefreshing(false);
                ArrayList<Device> devices = toDevices(deviceList);
                subScribeProperty(devices);
                subScribeOnlineStatus(devices);
                DeviceManager.getInstance().clear();
                DeviceManager.getInstance().addAll(devices);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void error(ACException e) {
                binding.swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private ArrayList<Device> toDevices(List<ACUserDevice> deviceList) {
        ArrayList<Device> devices = new ArrayList<>();
        for (ACUserDevice acUserDevice : deviceList) {
            Device device = new Device(acUserDevice.getSubDomain(), acUserDevice.getDeviceId(), acUserDevice.getPhysicalDeviceId(), acUserDevice.getStatus() != ACUserDevice.OFFLINE, false);
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


    private class DeviceListAdapter extends RecyclerView.Adapter<com.accloud.ac_service_android_demo.databinding.BindingHolder> {

        private ArrayList<Device> devices = DeviceManager.getInstance().getDevices();

        @Override
        public com.accloud.ac_service_android_demo.databinding.BindingHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            return new com.accloud.ac_service_android_demo.databinding.BindingHolder(DataBindingUtil.inflate(inflater, R.layout.itemview_device_list, parent, false));
        }

        @Override
        public void onBindViewHolder(com.accloud.ac_service_android_demo.databinding.BindingHolder holder, int position) {
            final Device device = getItem(position);
            ItemviewDeviceListBinding binding = (ItemviewDeviceListBinding) holder.binding;
            binding.setDevice(device);
            setListener(device, binding);
        }

        @Override
        public int getItemCount() {
            return devices.size();
        }

        public ArrayList<Device> getItems() {
            return devices;
        }

        public Device getItem(int position) {
            return devices.get(position);
        }
    }

    private void setListener(final Device device, final ItemviewDeviceListBinding binding) {
        binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(DeviceListActivity.this, DeviceControlActivity.class).putExtra(IntentKey.DEVICE_ID, device.getDeviceId()),
                        REQUEST_CODE_CONTROL_DEVICE);
            }
        });
        binding.lightSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (device.isPowerOn()) {
                    DeviceApi.closeLight(device.getPhysicalDeviceId(), new VoidCallback() {
                        @Override
                        public void success() {

                        }

                        @Override
                        public void error(ACException e) {
                            binding.lightSwitch.setChecked(true);
                        }
                    });
                } else {
                    DeviceApi.openLight(device.getPhysicalDeviceId(), new VoidCallback() {
                        @Override
                        public void success() {

                        }

                        @Override
                        public void error(ACException e) {
                            binding.lightSwitch.setChecked(false);
                        }
                    });
                }
            }
        });
    }
}
