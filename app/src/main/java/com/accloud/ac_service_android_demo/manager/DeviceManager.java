package com.accloud.ac_service_android_demo.manager;

import com.accloud.ac_service_android_demo.model.Device;

import java.util.ArrayList;

/**
 * Created by liuxiaofeng on 08/09/2017.
 */

public class DeviceManager {

    private ArrayList<Device> devices = new ArrayList<>();

    private DeviceManager() {
    }

    private static final class DeviceManagerHolder {

        public static final DeviceManager sDeviceManager = new DeviceManager();

    }

    public static DeviceManager getInstance() {
        return DeviceManagerHolder.sDeviceManager;
    }

    public ArrayList<Device> getDevices() {
        return devices;
    }

    public Device getDevice(long deviceId) {
        for (Device device : devices) {
            if (device.getDeviceId() == deviceId) {
                return device;
            }
        }
        return null;
    }

    public void clear() {
        devices.clear();
    }

    public void addAll(ArrayList<Device> devices) {
        this.devices.addAll(devices);
    }
}
