package ablecloud.matrix.app.demo;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

import ablecloud.matrix.MatrixCallback;
import ablecloud.matrix.MatrixError;
import ablecloud.matrix.app.Matrix;
import ablecloud.matrix.app.demo.databinding.ActivityListBinding;
import ablecloud.matrix.model.Device;
import ablecloud.support.databinding.BindingHolder;
import ablecloud.support.databinding.CountObservable;
import ablecloud.support.widget.ArrayRecyclerAdapter;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

/**
 * Created by wangkun on 24/07/2017.
 */

public class DeviceListActivity extends AppCompatActivity {

    private ActivityListBinding binding;
    private DeviceAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_list);
        adapter = new DeviceAdapter();
        binding.list.setAdapter(adapter);
        binding.setCountObservable(CountObservable.create(adapter));
        updateList();
    }

    private void updateList() {
        Single.create(new SingleOnSubscribe<List<Device>>() {
            @Override
            public void subscribe(final SingleEmitter<List<Device>> emitter) throws Exception {
                Matrix.bindManager().listDevices(new MatrixCallback<List<Device>>() {
                    @Override
                    public void success(List<Device> devices) {
                        emitter.onSuccess(devices);
                    }

                    @Override
                    public void error(MatrixError error) {
                        emitter.onError(error);
                    }
                });
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<Device>>() {
            @Override
            public void accept(@NonNull List<Device> devices) throws Exception {
                adapter.clear();
                adapter.addAll(devices);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(@NonNull Throwable throwable) throws Exception {
                Toast.makeText(DeviceListActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private static class DeviceAdapter extends ArrayRecyclerAdapter<Device, BindingHolder> {
        @Override
        public BindingHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return BindingHolder.create(R.layout.list_item_device, parent);
        }

        @Override
        public void onBindViewHolder(BindingHolder holder, int position) {
            holder.binding.setVariable(BR.position, position);
            holder.binding.setVariable(BR.physicalDeviceId, getItem(position).physicalDeviceId);
        }
    }
}
