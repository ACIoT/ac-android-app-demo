package ablecloud.matrix.app;

import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.util.Log;

import com.accloud.cloudservice.AC;
import com.accloud.cloudservice.PayloadCallback;
import com.accloud.service.ACException;
import com.accloud.service.ACUserDevice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by wangkun on 10/24/2016.
 */

public class DeviceManager {
    private static final String TAG = "DeviceManager";

    private List<ACUserDevice> mDevices = new ArrayList<>();
    private DataSetObservable mDataSetObservable = new DataSetObservable();

    public void registerDataSetObserver(DataSetObserver observer) {
        mDataSetObservable.registerObserver(observer);
    }

    public void unregisterDataSetObserver(DataSetObserver observer) {
        mDataSetObservable.unregisterObserver(observer);
    }

    public List<ACUserDevice> getDevices() {
        return Collections.unmodifiableList(mDevices);
    }

    public void queryDevices() {
        AC.bindMgr().listDevicesWithStatus(new PayloadCallback<List<ACUserDevice>>() {
            @Override
            public void success(List<ACUserDevice> acUserDevices) {
                mDevices.clear();
                mDevices.addAll(acUserDevices);
                mDataSetObservable.notifyChanged();
            }

            @Override
            public void error(ACException e) {
                Log.e(TAG, "listDevicesWithStatus " + e.getMessage());
            }
        });
    }
}
