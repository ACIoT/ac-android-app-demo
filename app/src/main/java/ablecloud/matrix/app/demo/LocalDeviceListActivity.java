package ablecloud.matrix.app.demo;

import android.database.DataSetObserver;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import ablecloud.matrix.DeviceMessage;
import ablecloud.matrix.app.demo.databinding.ActivityListBinding;
import ablecloud.matrix.local.LocalDevice;
import ablecloud.matrix.local.LocalDeviceManager;
import ablecloud.matrix.local.MatrixLocal;
import ablecloud.support.databinding.BindingHolder;
import ablecloud.support.databinding.CountObservable;
import ablecloud.support.widget.ArrayRecyclerAdapter;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import okio.ByteString;

public class LocalDeviceListActivity extends AppCompatActivity {

    private ActivityListBinding binding;

    private LocalDeviceManager manager;
    private LocalDeviceAdapter adapter;

    private DataSetObserver deviceObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            Completable.complete().observeOn(AndroidSchedulers.mainThread()).subscribe(new Action() {
                @Override
                public void run() throws Exception {
                    updateList(manager.getWatchedDevices());
                }
            });
        }
    };

    private LocalDeviceManager.DataReceiver dataReceiver = new LocalDeviceManager.DataReceiver() {
        @Override
        public void onDataReceive(long subDomainId, String physicalDeviceId, DeviceMessage deviceMessage) {
            for (int i = 0; i < adapter.getItemCount(); i++) {
                LocalDeviceInfo item = adapter.getItem(i);
                if (item.device.physicalDeviceId.equals(physicalDeviceId)) {
                    item.data = ByteString.of(deviceMessage.getContent()).hex();
                    final int index = i;
                    Completable.complete().observeOn(AndroidSchedulers.mainThread()).subscribe(new Action() {
                        @Override
                        public void run() throws Exception {
                            adapter.notifyItemChanged(index);
                        }
                    });
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_list);
        manager = MatrixLocal.localDeviceManager();
        manager.registerLocalDeviceObserver(deviceObserver);

        adapter = new LocalDeviceAdapter();
        binding.list.setAdapter(adapter);
        binding.setCountObservable(CountObservable.create(adapter));
    }

    private void updateList(List<LocalDevice> watchedDevices) {
        ArrayList<LocalDeviceInfo> infos = new ArrayList<>();
        for (LocalDevice device : watchedDevices) {
            infos.add(new LocalDeviceInfo(device));
        }
        adapter.clear();
        adapter.addAll(infos);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        manager.unregisterLocalDeviceObserver(deviceObserver);
    }

    @Override
    protected void onStart() {
        super.onStart();
        manager.registerLocalDataReceiver(dataReceiver);
    }

    @Override
    protected void onStop() {
        super.onStop();
        manager.unregisterLocalDataReceiver(dataReceiver);
    }

    private static class LocalDeviceAdapter extends ArrayRecyclerAdapter<LocalDeviceInfo, BindingHolder> {
        @Override
        public BindingHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return BindingHolder.create(R.layout.list_item_device, parent);
        }

        @Override
        public void onBindViewHolder(BindingHolder holder, int position) {
            holder.binding.setVariable(BR.position, position);
            LocalDeviceInfo item = getItem(position);
            holder.binding.setVariable(BR.physicalDeviceId, item.device.physicalDeviceId);
            holder.binding.setVariable(BR.data, item.data);
        }
    }

    private static class LocalDeviceInfo {
        public final LocalDevice device;
        public String data;

        public LocalDeviceInfo(LocalDevice device) {
            this.device = device;
        }
    }
}
