package com.accloud.ac_service_android_demo.activity;

import android.app.Activity;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.accloud.ac_service_android_demo.R;
import com.accloud.ac_service_android_demo.config.Config;
import com.accloud.ac_service_android_demo.utils.Pop;
import com.accloud.cloudservice.AC;
import com.accloud.cloudservice.ACDeviceActivator;
import com.accloud.cloudservice.PayloadCallback;
import com.accloud.service.ACDeviceBind;
import com.accloud.service.ACException;
import com.accloud.service.ACUserDevice;
import com.accloud.service.ACWifiInfo;
import com.accloud.utils.PreferencesUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 通过AP配网并绑定设备
 */
public class APDeviceActivity extends Activity implements View.OnClickListener {

    private TextView back;
    private Spinner firstSpinner;
    private Spinner secondSpinner;
    private ArrayAdapter firstAdapter;
    private ArrayAdapter secondAdapter;

    private TextView firstRefresh;
    private TextView secondRefresh;
    private Button connect;
    private Button bindDevice;
    private EditText password;
    private EditText deviceName;

    private String physicalDeviceId;

    ACDeviceActivator deviceActivator;
    String mySsid;

    WifiManager wifiManager;
    List<ScanResult> scanResultList;
    List<String> allSsidList;
    List<String> useableSsidList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ap_device);

        initView();
        initSpinnerView();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_device_back:
                finish();
                break;
            case R.id.first_refresh:
                getSsidList();
                Pop.popToast(this, getString(R.string.ap_link_aty_refresh_available_hotspot));
                firstAdapter.notifyDataSetChanged();
                break;
            case R.id.second_refresh:
                searchAvailableWifi(false);
                break;
            case R.id.connect:
                if (mySsid == null) {
                    Pop.popToast(this, getString(R.string.ap_link_aty_connectbtn_pressed_toast_hint));
                    return;
                }
                connect.setText(getString(R.string.ap_link_aty_connectbtn_pressed_device_activate_ing, mySsid));
                connect.setEnabled(false);
                startApLink();
                break;
            case R.id.bindDevice:
                if (physicalDeviceId == null) {
                    Pop.popToast(this, getString(R.string.ap_link_aty_binddevicebtn_pressed_toast_hint));
                    return;
                }
                bindDevice();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (deviceActivator.isAbleLink())
            deviceActivator.stopAbleLink();
    }

    private void initView() {
        back = (TextView) findViewById(R.id.add_device_back);
        firstRefresh = (TextView) findViewById(R.id.first_refresh);
        secondRefresh = (TextView) findViewById(R.id.second_refresh);
        connect = (Button) findViewById(R.id.connect);
        bindDevice = (Button) findViewById(R.id.bindDevice);
        password = (EditText) findViewById(R.id.edit_pwd);
        deviceName = (EditText) findViewById(R.id.edit_name);

        back.setOnClickListener(this);
        firstRefresh.setOnClickListener(this);
        secondRefresh.setOnClickListener(this);
        connect.setOnClickListener(this);
        bindDevice.setOnClickListener(this);
        deviceActivator = AC.deviceActivator(AC.DEVICE_AP);
    }

    private void initSpinnerView() {
        allSsidList = new ArrayList<>();
        useableSsidList = new ArrayList<>();
        useableSsidList.add(getString(R.string.ap_link_aty_choosetext));

        getSsidList();

        firstSpinner = (Spinner) findViewById(R.id.first_spinner);
        secondSpinner = (Spinner) findViewById(R.id.second_spinner);
        firstAdapter = new ArrayAdapter<>(this, R.layout.spinner_text, allSsidList);
        secondAdapter = new ArrayAdapter<>(this, R.layout.spinner_text, useableSsidList);
        firstAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        secondAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        firstSpinner.setAdapter(firstAdapter);
        secondSpinner.setAdapter(secondAdapter);
        firstSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    ((TextView) view).setTextColor(getResources().getColor(R.color.theme));
                    return;
                }
                String ssid = allSsidList.get(position);
                if (deviceActivator.getSSID().equals(ssid)) {
                    searchAvailableWifi(false);
                } else {
                    Pop.popToast(APDeviceActivity.this, getString(R.string.ap_link_aty_begin_autolink_hotspot));

                    WifiConfiguration config = new WifiConfiguration();
                    config.SSID = "\"" + ssid + "\"";
                    config.preSharedKey = "\"" + "123456789" + "\"";
                    config.hiddenSSID = true;
                    config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                    config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
                    config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                    config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                    config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                    config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                    config.status = WifiConfiguration.Status.ENABLED;

                    int netId = wifiManager.addNetwork(config);
                    wifiManager.disconnect();
                    boolean b = wifiManager.enableNetwork(netId, true);
                    if (b) {
                        searchAvailableWifi(true);
                    } else {
                        Pop.popToast(APDeviceActivity.this, getString(R.string.ap_link_aty_autolink_hotspot_failed));
                        firstSpinner.setSelection(0);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        secondSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    ((TextView) view).setTextColor(getResources().getColor(R.color.theme));
                    return;
                }
                mySsid = useableSsidList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void getSsidList() {
        wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        scanResultList = wifiManager.getScanResults();
        if (scanResultList != null && scanResultList.size() > 0) {
            allSsidList.clear();
            allSsidList.add(getString(R.string.ap_link_aty_choosetext));
            for (int i = 0; i < scanResultList.size(); i++) {
                ScanResult scanResult = scanResultList.get(i);
                allSsidList.add(scanResult.SSID);
            }
        } else {
            Pop.popToast(this, getString(R.string.ap_link_aty_no_available_hotspot));
            finish();
        }
    }

    //搜索设备可用的热点
    private void searchAvailableWifi(boolean wait) {
        if (wait) {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
            }
        }
        Pop.popToast(APDeviceActivity.this, getString(R.string.ap_link_aty_get_wifilist_ing));
        secondRefresh.setClickable(false);
        secondRefresh.setTextColor(getResources().getColor(R.color.grey));
        deviceActivator.searchAvailableWifi(10000, new PayloadCallback<List<ACWifiInfo>>() {
            @Override
            public void success(List<ACWifiInfo> wifiInfos) {
                secondRefresh.setClickable(true);
                secondRefresh.setTextColor(getResources().getColor(R.color.theme));
                if (wifiInfos != null && wifiInfos.size() > 0) {
                    useableSsidList.clear();
                    useableSsidList.add(getString(R.string.ap_link_aty_choosetext));
                    for (ACWifiInfo wifiInfo : wifiInfos) {
                        useableSsidList.add(wifiInfo.getSsid());
                    }
                    secondAdapter.notifyDataSetChanged();
                    Pop.popToast(APDeviceActivity.this, getString(R.string.ap_link_aty_to_second_step_toast_hint));
                } else {
                    Pop.popToast(APDeviceActivity.this, getString(R.string.ap_link_aty_device_no_wifi_hotspot_toast_hint));
                    finish();
                }
            }

            @Override
            public void error(ACException e) {
                Pop.popToast(APDeviceActivity.this, getString(R.string.ap_link_aty_please_click_refreshbtn_in_second_step));
                secondRefresh.setClickable(true);
                secondRefresh.setTextColor(getResources().getColor(R.color.theme));
            }
        });
    }

    //配置目标热点
    public void startApLink() {
        deviceActivator.startApLink(mySsid, password.getText().toString(), AC.DEVICE_ACTIVATOR_DEFAULT_TIMEOUT, null, new PayloadCallback<ACDeviceBind>() {
            @Override
            public void success(ACDeviceBind deviceBind) {
                Pop.popToast(APDeviceActivity.this, getString(R.string.ap_link_aty_device_activate_success_toast_hint));
                physicalDeviceId = deviceBind.getPhysicalDeviceId();
                connect.setText(R.string.ap_link_aty_activate_success_you_can_bind);
            }

            @Override
            public void error(ACException e) {
                Pop.popToast(APDeviceActivity.this, getString(R.string.ap_link_aty_device_activate_fail_toast_hint, e.toString()));
                finish();
            }
        });
    }

    //绑定设备
    public void bindDevice() {
        AC.bindMgr().bindDevice(PreferencesUtils.getString(this, "subDomain", Config.SUBDOMAIN), physicalDeviceId, deviceName.getText().toString(), new PayloadCallback<ACUserDevice>() {
            @Override
            public void success(ACUserDevice userDevice) {
                Pop.popToast(APDeviceActivity.this, getString(R.string.ap_link_aty_device_bind_success, userDevice.getDeviceId()));
                APDeviceActivity.this.finish();
            }

            @Override
            public void error(ACException e) {
                Pop.popToast(APDeviceActivity.this, getString(R.string.ap_link_aty_device_bind_fail, e.toString()));
            }
        });
    }
}
