package ablecloud.matrix.app.demo;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.accloud.cloudservice.AC;
import com.accloud.cloudservice.PayloadCallback;
import com.accloud.service.ACException;
import com.accloud.service.ACUserDevice;

import java.util.List;

import ablecloud.matrix.app.demo.databinding.ActivityMainBinding;
import ablecloud.matrix.app.demo.databinding.ListItemDeviceBinding;
import ablecloud.support.databinding.BindingHolder;
import ablecloud.support.databinding.CountObservable;
import ablecloud.support.widget.ArrayRecyclerAdapter;

/**
 * Created by wangkun on 31/03/2017.
 */

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private ActivityMainBinding mBinding;
    private DeviceAdapter mDeviceAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mDeviceAdapter = new DeviceAdapter();
        mBinding.list.getRefreshView().setAdapter(mDeviceAdapter);
        mBinding.setCountObservable(CountObservable.create(mDeviceAdapter));

        AC.bindMgr().listDevicesWithStatus(mDeviceCallback);
    }

    private PayloadCallback<List<ACUserDevice>> mDeviceCallback = new PayloadCallback<List<ACUserDevice>>() {
        @Override
        public void success(List<ACUserDevice> acUserDevices) {
            mDeviceAdapter.clear();
            mDeviceAdapter.addAll(acUserDevices);
        }

        @Override
        public void error(ACException e) {
            Log.d(TAG, e.getMessage());
        }
    };

    private static class DeviceAdapter extends ArrayRecyclerAdapter<ACUserDevice, BindingHolder> {
        @Override
        public BindingHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            return new BindingHolder<>(DataBindingUtil.inflate(inflater, R.layout.list_item_device, parent, false));
        }

        @Override
        public void onBindViewHolder(BindingHolder holder, int position) {
            ListItemDeviceBinding binding = (ListItemDeviceBinding) holder.binding;
            binding.setVariable(BR.device, getItem(position));
            binding.executePendingBindings();
        }
    }
}
