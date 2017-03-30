package com.accloud.ac_service_android_demo.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.accloud.ac_service_android_demo.R;
import com.accloud.ac_service_android_demo.config.Config;
import com.accloud.ac_service_android_demo.controller.Light;
import com.accloud.ac_service_android_demo.utils.Pop;
import com.accloud.ac_service_android_demo.utils.ViewHolder;
import com.accloud.ac_service_android_demo.utils.XListView;
import com.accloud.cloudservice.AC;
import com.accloud.cloudservice.PayloadCallback;
import com.accloud.cloudservice.VoidCallback;
import com.accloud.service.ACBindMgr;
import com.accloud.service.ACDeviceFind;
import com.accloud.service.ACException;
import com.accloud.service.ACOTACheckInfo;
import com.accloud.service.ACOTAUpgradeInfo;
import com.accloud.service.ACUserDevice;
import com.accloud.utils.PreferencesUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 主页面
 * <p/>
 * Created by sudongsheng on 2015/1/27.
 */
public class MainActivity extends Activity implements View.OnClickListener {

    private TextView menu;
    private TextView noDevice;
    private TextView addDevice;
    private XListView listView;
    private MyAdapter adapter;
    //设备管理器
    ACBindMgr bindMgr;

    private String subDomain;

    Timer timer;
    boolean isRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        menu = (TextView) findViewById(R.id.left_menu);
        noDevice = (TextView) findViewById(R.id.no_device);
        addDevice = (TextView) findViewById(R.id.right_add_device);
        listView = (XListView) findViewById(R.id.device_list);

        noDevice.setVisibility(View.GONE);
        menu.setOnClickListener(this);
        addDevice.setOnClickListener(this);

        //获取设备管理器
        bindMgr = AC.bindMgr();

        adapter = new MyAdapter(this);
        listView.setAdapter(adapter);
        listView.setXListViewListener(new XListView.IXListViewListener() {
            @Override
            public void onRefresh() {
                getDeviceList();
            }
        });

        //检查设备是否有OTA升级
        checkOTAUpdates();
    }

    @Override
    protected void onResume() {
        super.onResume();

        subDomain = PreferencesUtils.getString(this, "subDomain", Config.SUBDOMAIN);
        noDevice.setVisibility(View.GONE);
        if (!AC.accountMgr().isLogin()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            getDeviceList();
        }
    }

    @Override
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.left_menu:
                intent = new Intent(MainActivity.this, MenuActivity.class);
                break;
            case R.id.right_add_device:
                intent = new Intent(MainActivity.this, AddDeviceActivity.class);
                break;
        }
        startActivity(intent);
    }

    class MyAdapter extends BaseAdapter {
        public List<ACUserDevice> deviceList;
        private Context context;
        private Light light;

        public MyAdapter(Context context) {
            this.context = context;
            light = new Light(context);
            deviceList = new ArrayList<>();
        }

        @Override
        public int getCount() {
            return deviceList.size();
        }

        @Override
        public Object getItem(int i) {
            return deviceList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            view = LayoutInflater.from(context).inflate(R.layout.adapter_list_device, null);
            TextView deviceName = ViewHolder.get(view, R.id.deviceName);
            Button openBtn = ViewHolder.get(view, R.id.openLight);
            Button closeBtn = ViewHolder.get(view, R.id.closeLight);

            final ACUserDevice device = deviceList.get(i);
            switch (device.getStatus()) {
                case ACUserDevice.OFFLINE:
                    deviceName.setTextColor(Color.GRAY);
                    deviceName.setText(getString(R.string.main_aty_device_offline, device.getName()));
                    break;
                case ACUserDevice.NETWORK_ONLINE:
                    deviceName.setTextColor(Color.GREEN);
                    deviceName.setText(getString(R.string.main_aty_device_cloud_online, device.getName()));
                    break;
                case ACUserDevice.LOCAL_ONLINE:
                    deviceName.setTextColor(Color.GREEN);
                    deviceName.setText(getString(R.string.main_aty_device_lan_online, device.getName()));
                    break;
                case ACUserDevice.BOTH_ONLINE:
                    deviceName.setTextColor(Color.GREEN);
                    deviceName.setText(getString(R.string.main_aty_device_cloud_and_lan_online, device.getName()));
                    break;
            }
            openBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    light.openLight(device.getPhysicalDeviceId());
                }
            });
            closeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    light.closeLight(device.getPhysicalDeviceId());
                }
            });
            deviceName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    int protocol = PreferencesUtils.getInt(MainActivity.this, "formatType", ConfigurationActivity.BINARY);
                    if (protocol == ConfigurationActivity.BINARY) {
                        intent.setClass(MainActivity.this, CustomBinaryActivity.class);
                    } else
                        intent.setClass(MainActivity.this, CustomJsonActivity.class);
                    intent.putExtra("deviceId", device.getDeviceId());
                    intent.putExtra("physicalDeviceId", device.getPhysicalDeviceId());
                    startActivity(intent);
                }
            });
            deviceName.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    new AlertDialog.Builder(MainActivity.this).setTitle(getString(R.string.main_aty_delete_device_title)).setMessage(getString(R.string.main_aty_delete_device_desc))
                            .setNegativeButton(getString(R.string.main_aty_delete_device_cancle), null)
                            .setPositiveButton(getString(R.string.main_aty_delete_device_confirm), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int m) {
                                    unbindDevice(device);
                                }
                            }).show();
                    return false;
                }
            });
            return view;
        }
    }

    //获取设备列表
    public void getDeviceList() {
        bindMgr.listDevicesWithStatus(new PayloadCallback<List<ACUserDevice>>() {
            @Override
            public void success(List<ACUserDevice> deviceList) {
                for (ACUserDevice device : deviceList) {
                    device.getStatus();
                }
                if (deviceList.size() == 0) {
                    noDevice.setVisibility(View.VISIBLE);
                    listView.setPullRefreshEnable(false);
                } else {
                    listView.setPullRefreshEnable(true);
                }
                adapter.deviceList = deviceList;
                adapter.notifyDataSetChanged();
                listView.stopRefresh();
                //启动定时器,定时更新局域网状态
                startTimer();
            }

            @Override
            public void error(ACException e) {
            }
        });
    }

    //删除设备
    public void unbindDevice(ACUserDevice device) {
        bindMgr.unbindDevice(subDomain, device.getDeviceId(), new VoidCallback() {
            @Override
            public void success() {
                Pop.popToast(MainActivity.this, getString(R.string.main_aty_delete_device_success));
                getDeviceList();
            }

            @Override
            public void error(ACException e) {
                Pop.popToast(MainActivity.this, e.getErrorCode() + "-->" + e.getMessage());
            }
        });
    }

    //定时更新设备当前的局域网状态
    public void refreshDeviceStatus() {
        //当设备掉线或网络环境不稳定导致获取局域网显示状态不准确时，需要手动刷新设备列表与局域网状态
        AC.findLocalDevice(AC.FIND_DEVICE_DEFAULT_TIMEOUT, new PayloadCallback<List<ACDeviceFind>>() {
            @Override
            public void success(List<ACDeviceFind> deviceFinds) {
                //局域网状态是否发生改变,是否需要更新界面
                boolean isRefresh = false;
                //遍历当前用户绑定的所有设备列表
                for (ACUserDevice device : adapter.deviceList) {
                    //判断当前设备是否局域网本地在线
                    boolean isLocalOnline = false;
                    //遍历当前发现的局域网在线列表
                    for (ACDeviceFind deviceFind : deviceFinds) {
                        //通过设备的物理Id进行匹配,若当前设备在发现的局域网列表中,则置为局域网在线
                        if (device.getPhysicalDeviceId().equals(deviceFind.getPhysicalDeviceId())) {
                            isLocalOnline = true;
                        }
                    }
                    if (isLocalOnline) {
                        //当前设备由不在线更新为局域网在线
                        if (device.getStatus() == ACUserDevice.OFFLINE) {
                            device.setStatus(ACUserDevice.LOCAL_ONLINE);
                            isRefresh = true;
                            //当前设备由云端在线更新为云端局域网同时在线
                        } else if (device.getStatus() == ACUserDevice.NETWORK_ONLINE) {
                            device.setStatus(ACUserDevice.BOTH_ONLINE);
                            isRefresh = true;
                        }
                    } else {
                        //当前设备由局域网在线更新为不在线
                        if (device.getStatus() == ACUserDevice.LOCAL_ONLINE) {
                            device.setStatus(ACUserDevice.OFFLINE);
                            AC.SEND_TO_LOCAL_DEVICE_DEFAULT_TIMEOUT = 6000;
                            isRefresh = true;
                            //当前设备由云端局域网同时在线更新为云端在线
                        } else if (device.getStatus() == ACUserDevice.BOTH_ONLINE) {
                            device.setStatus(ACUserDevice.NETWORK_ONLINE);
                            isRefresh = true;
                        }
                    }
                }
                //局域网状态需要发生改变,更新列表界面
                if (isRefresh)
                    adapter.notifyDataSetChanged();
            }

            @Override
            public void error(ACException e) {
                //局域网状态是否发生改变,是否需要更新列表界面
                boolean isRefresh = false;
                for (ACUserDevice device : adapter.deviceList) {
                    //没有设备当前局域网在线,所以把所有当前显示局域网在线的设备状态重置
                    if (device.getStatus() == ACUserDevice.LOCAL_ONLINE) {
                        device.setStatus(ACUserDevice.OFFLINE);
                        isRefresh = true;
                    } else if (device.getStatus() == ACUserDevice.BOTH_ONLINE) {
                        device.setStatus(ACUserDevice.NETWORK_ONLINE);
                        isRefresh = true;
                    }
                }
                //局域网状态需要发生改变,更新列表界面
                if (isRefresh)
                    adapter.notifyDataSetChanged();
            }
        });
    }

    //启动定时器,定时更新局域网状态
    private void startTimer() {
        if (!isRunning) {
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    refreshDeviceStatus();
                }
            }, 0, 3000);
            isRunning = true;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopTimer();
    }

    //关闭局域网在线状态定时器
    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
            isRunning = false;
        }
    }

    //检查所有设备是否有OTA升级
    private void checkOTAUpdates() {
        //获取所有设备列表
        AC.bindMgr().listDevices(new PayloadCallback<List<ACUserDevice>>() {
            @Override
            public void success(List<ACUserDevice> devices) {
                //遍历所有绑定的设备
                for (final ACUserDevice device : devices) {
                    ACOTACheckInfo checkInfo = new ACOTACheckInfo(device.getDeviceId(), 1);
                    //检查该设备是否有OTA升级
                    AC.otaMgr().checkUpdate(subDomain, checkInfo, new PayloadCallback<ACOTAUpgradeInfo>() {
                        @Override
                        public void success(ACOTAUpgradeInfo upgradeInfo) {
                            //如果有OTA新版本,则弹框显示是否确认升级
                            if (upgradeInfo.isUpdate())
                                showOTADialog(device, upgradeInfo);
                        }

                        @Override
                        public void error(ACException e) {
                        }
                    });
                }
            }

            @Override
            public void error(ACException e) {
            }
        });
    }

    //如果有OTA新版本升级,则弹框显示
    private void showOTADialog(final ACUserDevice device, final ACOTAUpgradeInfo info) {
        new AlertDialog.Builder(this).setTitle(R.string.main_aty_ota_upgrade_title)
                .setMessage(getString(R.string.main_aty_ota_upgrade_desc, device.getPhysicalDeviceId(),
                        device.getName(), info.getTargetVersion(), info.getUpgradeLog()))
                .setPositiveButton(getString(R.string.main_aty_ota_upgrade_confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //确认升级
                        AC.otaMgr().confirmUpdate(subDomain, device.getDeviceId(), info.getTargetVersion(), 1, new VoidCallback() {
                            @Override
                            public void success() {
                                Pop.popToast(MainActivity.this, getString(R.string.main_aty_ota_upgrade_toast_hint));
                            }

                            @Override
                            public void error(ACException e) {
                                Pop.popToast(MainActivity.this, e.toString());
                            }
                        });
                    }
                }).setNegativeButton(getString(R.string.main_aty_ota_upgrade_cancle), null)
                .create().show();
    }
}
