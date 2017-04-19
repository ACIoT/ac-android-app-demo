package ablecloud.matrix.app.demo;

import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.accloud.cloudservice.AC;
import com.accloud.cloudservice.PayloadCallback;
import com.accloud.service.ACException;
import com.accloud.service.ACProduct;
import com.accloud.service.ACUserDevice;

import java.util.ArrayList;
import java.util.List;

import ablecloud.matrix.app.demo.databinding.ActivityMainBinding;
import ablecloud.matrix.app.demo.databinding.ListItemDeviceBinding;
import ablecloud.matrix.app.demo.util.IntentExtra;
import ablecloud.support.databinding.BindingHolder;
import ablecloud.support.databinding.CountObservable;
import ablecloud.support.widget.ArrayRecyclerAdapter;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrUIHandler;
import in.srain.cube.views.ptr.indicator.PtrIndicator;

/**
 * Created by wangkun on 31/03/2017.
 */

public class MainActivity extends BaseActivity {
    private static final String TAG = "MainActivity";

    private ActivityMainBinding binding;
    private DeviceAdapter deviceAdapter;

    private List<ACProduct> products = new ArrayList<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        deviceAdapter = new DeviceAdapter();
        binding.list.getRefreshView().setAdapter(deviceAdapter);
        binding.list.addPtrUIHandler(ptrUIHandler);
        binding.setCountObservable(CountObservable.create(deviceAdapter));

        AC.productMgr().fetchAllProducts(mProductCallback);
        deviceManager.registerDataSetObserver(deviceObserver);
        deviceManager.queryDevices();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        deviceManager.unregisterDataSetObserver(deviceObserver);
    }

    private PtrUIHandler ptrUIHandler = new PtrUIHandler() {
        @Override
        public void onUIReset(PtrFrameLayout frame) {

        }

        @Override
        public void onUIRefreshPrepare(PtrFrameLayout frame) {

        }

        @Override
        public void onUIRefreshBegin(PtrFrameLayout frame) {
            deviceManager.queryDevices();
        }

        @Override
        public void onUIRefreshComplete(PtrFrameLayout frame) {

        }

        @Override
        public void onUIPositionChange(PtrFrameLayout frame, boolean isUnderTouch, byte status, PtrIndicator ptrIndicator) {

        }
    };

    private DataSetObserver deviceObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            deviceAdapter.clear();
            deviceAdapter.addAll(deviceManager.getDevices());
        }
    };

    private PayloadCallback<List<ACProduct>> mProductCallback = new PayloadCallback<List<ACProduct>>() {
        @Override
        public void success(List<ACProduct> acProducts) {
            products.clear();
            products.addAll(acProducts);
            deviceAdapter.notifyDataSetChanged();
        }

        @Override
        public void error(ACException e) {
            Log.d(TAG, e.getMessage());
        }
    };

    private class DeviceAdapter extends ArrayRecyclerAdapter<ACUserDevice, BindingHolder> {
        @Override
        public BindingHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            return new BindingHolder<>(DataBindingUtil.inflate(inflater, R.layout.list_item_device, parent, false));
        }

        @Override
        public void onBindViewHolder(BindingHolder holder, int position) {
            ListItemDeviceBinding binding = (ListItemDeviceBinding) holder.binding;
            final ACUserDevice device = getItem(position);
            binding.setVariable(BR.device, device);
            binding.setVariable(BR.product, findProduct(device));
            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    context.startActivity(new Intent(context, DeviceControlActivity.class).putExtra(IntentExtra.DEVICE_ID, device.deviceId));
                }
            });
            binding.executePendingBindings();
        }
    }

    private ACProduct findProduct(ACUserDevice device) {
        for (ACProduct product : products) {
            if (product.sub_domain_name.equals(device.subDomain)) {
                return product;
            }
        }
        return null;
    }
}
