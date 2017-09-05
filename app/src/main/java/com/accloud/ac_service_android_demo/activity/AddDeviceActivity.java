package com.accloud.ac_service_android_demo.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.accloud.ac_service_android_demo.R;
import com.accloud.ac_service_android_demo.config.Config;
import com.accloud.ac_service_android_demo.utils.ToastUtil;
import com.accloud.cloudservice.AC;
import com.accloud.cloudservice.PayloadCallback;
import com.accloud.service.ACDeviceFind;
import com.accloud.service.ACException;
import com.accloud.service.ACUserDevice;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by liuxiaofeng on 05/09/2017.
 */

public class AddDeviceActivity extends AppCompatActivity {

    @BindView(R.id.swipe)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(android.R.id.list)
    ListView listView;

    @BindView(android.R.id.empty)
    TextView emptyView;

    private LocalDeviceAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("添加设备");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_add_device);
        ButterKnife.bind(this);
        swipeRefreshLayout.setOnRefreshListener(onRefreshListener);
        adapter = new LocalDeviceAdapter(AddDeviceActivity.this);
        listView.setAdapter(adapter);
        listView.setEmptyView(emptyView);
        emptyView.setText(getString(R.string.empty_list, getString(R.string.local_device)));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            adapter.clear();
            AC.findLocalDevice(AC.FIND_DEVICE_DEFAULT_TIMEOUT, new PayloadCallback<List<ACDeviceFind>>() {
                @Override
                public void success(final List<ACDeviceFind> acDeviceFinds) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.addAll(acDeviceFinds);
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    });
                }

                @Override
                public void error(final ACException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.show(AddDeviceActivity.this, "findDevice error: " + e.getMessage());
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    });
                }
            });
        }
    };


    private class LocalDeviceAdapter extends ArrayAdapter<ACDeviceFind> {
        public LocalDeviceAdapter(Context context) {
            super(context, 0);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
            }
            final ACDeviceFind acDeviceFind = getItem(position);
            StringBuilder builder = new StringBuilder();
            builder.append("physicalDeviceId: " + acDeviceFind.getPhysicalDeviceId() + "\n");
            builder.append("subDomainId: " + acDeviceFind.getSubDomainId() + "\n");
            builder.append("ipAddress: " + acDeviceFind.getIp());
            ((TextView) convertView.findViewById(android.R.id.text1)).setText(builder.toString());
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AC.bindMgr().bindDevice(Config.SUB_DOMAIN, acDeviceFind.getPhysicalDeviceId(), "智能灯", new PayloadCallback<ACUserDevice>() {
                        @Override
                        public void success(ACUserDevice acUserDevice) {
                            setResult(RESULT_OK);
                            AddDeviceActivity.this.finish();
                        }

                        @Override
                        public void error(ACException e) {
                            ToastUtil.show(AddDeviceActivity.this, "绑定失败" + e.getMessage());
                        }
                    });
                }
            });
            return convertView;
        }
    }
}
