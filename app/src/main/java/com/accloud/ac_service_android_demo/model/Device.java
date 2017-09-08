package com.accloud.ac_service_android_demo.model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.graphics.Color;

import com.accloud.ac_service_android_demo.BR;

/**
 * Created by liuxiaofeng on 01/09/2017.
 */

public class Device extends BaseObservable {

    private String subDomain;
    private long deviceId;
    private String physicalDeviceId;
    private boolean online;
    private boolean powerOn;

    public Device(String subDomain, long deviceId, String physicalDeviceId, boolean online, boolean powerOn) {
        this.subDomain = subDomain;
        this.deviceId = deviceId;
        this.physicalDeviceId = physicalDeviceId;
        this.online = online;
        this.powerOn = powerOn;
    }

    public String getSubDomain() {
        return subDomain;
    }

    public void setSubDomain(String subDomain) {
        this.subDomain = subDomain;
    }

    public long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(long deviceId) {
        this.deviceId = deviceId;
    }

    public String getPhysicalDeviceId() {
        return physicalDeviceId;
    }

    public void setPhysicalDeviceId(String physicalDeviceId) {
        this.physicalDeviceId = physicalDeviceId;
    }

    @Bindable
    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
        notifyPropertyChanged(BR.online);
        notifyPropertyChanged(BR.displayDesc);
        notifyPropertyChanged(BR.displayColor);
    }

    @Bindable
    public boolean isPowerOn() {
        return powerOn;
    }

    public void setPowerOn(boolean powerOn) {
        this.powerOn = powerOn;
        notifyPropertyChanged(BR.powerOn);
        notifyPropertyChanged(BR.switchBtnText);
    }

    @Bindable
    public String getDisplayDesc() {
        return physicalDeviceId + "(" + (isOnline() ? "在线" : "不在线") + ")";
    }

    @Bindable
    public int getDisplayColor() {
        return isOnline() ? Color.parseColor("#ff48f24f") : Color.parseColor("#ffaaaaaa");
    }

    @Bindable
    public String getSwitchBtnText() {
        return isPowerOn() ? "ON" : "OFF";
    }
}
