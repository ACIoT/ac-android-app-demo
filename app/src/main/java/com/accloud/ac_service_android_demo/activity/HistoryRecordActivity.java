package com.accloud.ac_service_android_demo.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.accloud.ac_service_android_demo.R;
import com.accloud.ac_service_android_demo.config.Config;
import com.accloud.ac_service_android_demo.model.LightPropertyRecord;
import com.accloud.ac_service_android_demo.utils.Pop;
import com.accloud.cloudservice.AC;
import com.accloud.cloudservice.PayloadCallback;
import com.accloud.cloudservice.VoidCallback;
import com.accloud.service.ACDeviceDataMgr;
import com.accloud.service.ACException;
import com.accloud.service.QueryOption;
import com.accloud.utils.PreferencesUtils;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 历史开关记录
 * <p/>
 * Created by Xuri on 2015/1/30.
 */
public class HistoryRecordActivity extends Activity {
    private ListView listView;
    private TextView back;
    private TextView reset;
    private MyAdapter adapter;
    private List<LightPropertyRecord> recordList = new ArrayList<>();
    private long deviceId;
    private String physicalDeviceId;
    private String subDomain;

    Gson gson = new Gson();
    ACDeviceDataMgr deviceDataMgr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deviceId = getIntent().getLongExtra("deviceId", 0);
        physicalDeviceId = getIntent().getStringExtra("physicalDeviceId");
        setContentView(R.layout.activity_history_record);
        subDomain = PreferencesUtils.getString(this, "subDomain", Config.SUBDOMAIN);
        back = (TextView) findViewById(R.id.history_back);
        reset = (TextView) findViewById(R.id.history_reset);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HistoryRecordActivity.this, ResetWifiActivity.class);
                intent.putExtra("physicalDeviceId", physicalDeviceId);
                startActivity(intent);
            }
        });
        listView = (ListView) findViewById(R.id.history_listView);
        adapter = new MyAdapter(this);
        listView.setAdapter(adapter);

        deviceDataMgr = AC.deviceDataMgr();
        //订阅设备数据
        subscribeRecord();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getRecord();
    }

    public void getRecord() {
        AC.deviceDataMgr().fetchHistoryProperty(subDomain, deviceId, new QueryOption(), new PayloadCallback<List<String>>() {
            @Override
            public void success(List<String> strings) {
                if (strings != null) {
                    for (String s : strings) {
                        recordList.add(gson.fromJson(s, LightPropertyRecord.class));
                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void error(ACException e) {
                Pop.popToast(HistoryRecordActivity.this, e.getErrorCode() + "-->" + e.getMessage());
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        deviceDataMgr.unSubscribeAllProperty();
        deviceDataMgr.unregisterPropertyReceiver(receiver);
    }

    public void subscribeRecord() {
        deviceDataMgr.subscribeProperty(subDomain, deviceId, new VoidCallback() {
            @Override
            public void success() {
                deviceDataMgr.registerPropertyReceiver(receiver);
                Pop.popToast(HistoryRecordActivity.this, getString(R.string.history_record_aty_subscribe_success));
            }

            @Override
            public void error(ACException e) {
                Pop.popToast(HistoryRecordActivity.this, getString(R.string.history_record_aty_subscribe_fail));
            }
        });
    }

    ACDeviceDataMgr.PropertyReceiver receiver = new ACDeviceDataMgr.PropertyReceiver() {
        @Override
        public void onPropertyReceive(String subDomain, long deviceId, String value) {
            // FIXME Subscribed property has no timestamp
            LightPropertyRecord record = gson.fromJson(value, LightPropertyRecord.class);
            record.timestamp = System.currentTimeMillis();
            recordList.add(0, record);
            adapter.notifyDataSetChanged();
        }
    };


    class MyAdapter extends BaseAdapter {
        Context context;

        public MyAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return recordList.size() + 1;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (i == 0) {
                view = LayoutInflater.from(context).inflate(R.layout.adapter_history_first_record, null);
            } else {
                view = LayoutInflater.from(context).inflate(R.layout.adapter_history_record, null);
                TextView dataTV = (TextView) view.findViewById(R.id.data);
                TextView actionTV = (TextView) view.findViewById(R.id.action);
                TextView sourceTV = (TextView) view.findViewById(R.id.type);
                LightPropertyRecord record = recordList.get(i - 1);
                dataTV.setText(new Date(record.timestamp).toLocaleString());
                actionTV.setText(record.light_on_off == 0 ? R.string.history_record_aty_close_light : R.string.history_record_aty_open_light);
                sourceTV.setText(record.source == 0 ? "from app" : "from switch");
            }
            return view;
        }
    }
}
