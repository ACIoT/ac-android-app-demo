package com.accloud.ac_service_android_demo.model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.graphics.Color;

import com.accloud.ac_service_android_demo.BR;
import com.accloud.service.ACUserDevice;

/**
 * Created by liuxiaofeng on 01/09/2017.
 */

public class Device extends BaseObservable {

    @Bindable
    private long deviceId;

    @Bindable
    private String physicalDeviceId;

    @Bindable
    private int onlineStatus;

    @Bindable
    private int lightStatus;

    public Device(long deviceId, String physicalDeviceId, int onlineStatus, int lightStatus) {
        this.deviceId = deviceId;
        this.physicalDeviceId = physicalDeviceId;
        this.onlineStatus = onlineStatus;
        this.lightStatus = lightStatus;
    }

    public long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(long deviceId) {
        this.deviceId = deviceId;
        notifyPropertyChanged(BR.deviceId);
    }

    public String getPhysicalDeviceId() {
        return physicalDeviceId;
    }

    public void setPhysicalDeviceId(String physicalDeviceId) {
        this.physicalDeviceId = physicalDeviceId;
        notifyPropertyChanged(BR.physicalDeviceId);
    }

    public int getOnlineStatus() {
        return onlineStatus;
    }

    public void setOnlineStatus(int onlineStatus) {
        this.onlineStatus = onlineStatus;
        notifyPropertyChanged(BR.onlineStatus);
    }

    public int getLightStatus() {
        return lightStatus;
    }

    public void setLightStatus(int lightStatus) {
        this.lightStatus = lightStatus;
        notifyPropertyChanged(BR.lightStatus);
    }

    public String getDisplayDesc() {
        String onlineDesc = "";
        switch (onlineStatus) {
            case ACUserDevice.NETWORK_ONLINE:
            case ACUserDevice.LOCAL_ONLINE:
            case ACUserDevice.BOTH_ONLINE:
                onlineDesc = "在线";
                break;
            default:
                onlineDesc = "不在线";
                break;
        }
        return physicalDeviceId + "(" + onlineDesc + ")";
    }

    public int getDisplayColor() {
        if (onlineStatus == ACUserDevice.OFFLINE) {
            return Color.parseColor("#ffaaaaaa");
        } else {
            return Color.parseColor("#ff48f24f");
        }
    }
}
