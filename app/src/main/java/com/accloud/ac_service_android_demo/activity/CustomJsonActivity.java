package com.accloud.ac_service_android_demo.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.accloud.ac_service_android_demo.R;
import com.accloud.ac_service_android_demo.application.MainApplication;
import com.accloud.ac_service_android_demo.config.Config;
import com.accloud.ac_service_android_demo.utils.Pop;
import com.accloud.ac_service_android_demo.utils.ViewHolder;
import com.accloud.cloudservice.AC;
import com.accloud.cloudservice.PayloadCallback;
import com.accloud.service.ACDeviceMsg;
import com.accloud.service.ACException;
import com.accloud.service.ACMsg;
import com.accloud.service.ACObject;
import com.accloud.utils.LogUtil;
import com.accloud.utils.PreferencesUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2015/7/13.
 */
public class CustomJsonActivity extends Activity implements View.OnClickListener {
    private TextView back;
    private TextView sendHistory;
    private Button sendBtn;
    private Spinner reqCode;
    private MyAdapter adapter;
    private ListView data_points;

    private int code;
    private boolean canSend = false;

    private String subDomain;
    private long deviceId;
    private String physicalDeviceId;
    HashMap<Integer, String> editMap = new HashMap<Integer, String>();
    ArrayAdapter<String> arrayAdapter;

    private void assignViews() {
        back = (TextView) findViewById(R.id.send_back);
        sendHistory = (TextView) findViewById(R.id.send_history);
        data_points = (ListView) findViewById(R.id.data_points);
        sendBtn = (Button) findViewById(R.id.cus_send);
        reqCode = (Spinner) findViewById(R.id.req_code);
        back.setOnClickListener(this);
        sendHistory.setOnClickListener(this);
        sendBtn.setOnClickListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        deviceId = getIntent().getLongExtra("deviceId", 0);
        physicalDeviceId = getIntent().getStringExtra("physicalDeviceId");
        super.onCreate(savedInstanceState);
        subDomain = PreferencesUtils.getString(MainApplication.getInstance(), "subDomain", Config.SUBDOMAIN);
        setContentView(R.layout.activity_cus_json);
        assignViews();

        adapter = new MyAdapter(this);
        data_points.setAdapter(adapter);

        arrayAdapter = new ArrayAdapter<String>(CustomJsonActivity.this, android.R.layout.simple_spinner_dropdown_item);
        arrayAdapter.add(getString(R.string.cus_json_aty_all));
        reqCode.setAdapter(arrayAdapter);
        reqCode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                editMap.clear();
                if (i == 0) {
                    canSend = false;
                    getAllDataPoints();
                } else {
                    code = Integer.parseInt(arrayAdapter.getItem(i));
                    getDataPoints(code);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                getAllDataPoints();
            }
        });
        getMessageCode();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.send_back:
                finish();
                break;
            case R.id.send_history:
                Intent intent = new Intent(CustomJsonActivity.this, HistoryRecordActivity.class);
                intent.putExtra("deviceId", deviceId);
                intent.putExtra("physicalDeviceId", physicalDeviceId);
                startActivity(intent);
                break;
            case R.id.cus_send:
                if (canSend)
                    sendData(code);
                else
                    Pop.popToast(CustomJsonActivity.this, getString(R.string.cus_json_aty_please_choose_data_package));
                break;
        }
    }

    public void sendData(int code) {
        JSONObject object = new JSONObject();
        for (int i = 0; i < adapter.dataPoints.size(); i++) {
            String valueStr = editMap.get(i);
            if (valueStr == null || valueStr.length() == 0) {
                continue;
            }
            Object value = null;
            switch (adapter.getDataType(i)) {
                case "bool":
                    try {
                        value = Boolean.valueOf(valueStr);
                    } catch (Exception e) {
                        value = false;
                    }
                    break;
                case "int8":
                case "int16":
                case "int32":
                case "int64":
                    try {
                        value = Long.valueOf(valueStr);
                    } catch (Exception e) {
                        value = 0;
                    }
                    break;
                case "float32":
                case "float64":
                    try {
                        value = Double.valueOf(valueStr);
                    } catch (Exception e) {
                        value = 0;
                    }
                    break;
                case "string":
                    value = valueStr;
                    break;
                default:
                    continue;
            }
            try {
                object.put(adapter.getDataName(i), value);
            } catch (JSONException e) {
            }
        }
        AC.bindMgr().sendToDeviceWithOption(subDomain, physicalDeviceId, new ACDeviceMsg(code, object.toString().getBytes()), AC.CLOUD_FIRST, new PayloadCallback<ACDeviceMsg>() {
            @Override
            public void success(ACDeviceMsg deviceMsg) {
                try {
                    JSONObject resp = new JSONObject(new String(deviceMsg.getContent()));
                    Pop.popToast(CustomJsonActivity.this, getString(R.string.cus_json_aty_send_success, resp.toString()));
                } catch (JSONException e) {
                }
            }

            @Override
            public void error(ACException e) {
                Pop.popToast(CustomJsonActivity.this, e.getErrorCode() + "-->" + e.getMessage());
            }
        });
    }

    public void getAllDataPoints() {
        ACMsg req = new ACMsg();
        req.setName("getSubDomainDataPoints");
        AC.sendToService(subDomain, "zc-product", 1, req, new PayloadCallback<ACMsg>() {
            @Override
            public void success(ACMsg resp) {
                List<ACObject> dataPoints = resp.get("datapoints");
                if (dataPoints == null || dataPoints.size() == 0)
                    return;
                adapter.dataPoints = dataPoints;
                adapter.notifyDataSetChanged();
            }

            @Override
            public void error(ACException e) {
                Pop.popToast(CustomJsonActivity.this, e.getErrorCode() + "-->" + e.getMessage());
            }
        });
    }

    public void getMessageCode() {
        ACMsg req = new ACMsg();
        req.setName("getSubDomainMessage");
        AC.sendToService(subDomain, "zc-product", 1, req, new PayloadCallback<ACMsg>() {
            @Override
            public void success(ACMsg resp) {
                List<ACObject> msgCodes = resp.get("messages");
                if (msgCodes == null || msgCodes.size() == 0) {
                    Pop.popToast(CustomJsonActivity.this, getString(R.string.cus_json_aty_please_create_data_package));
                    canSend = false;
                    return;
                }
                for (ACObject msgCodeObj : msgCodes)
                    arrayAdapter.add(msgCodeObj.get("code").toString());
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void error(ACException e) {
                Pop.popToast(CustomJsonActivity.this, e.getErrorCode() + "-->" + e.getMessage());
            }
        });
    }

    public void getDataPoints(int code) {
        ACMsg req = new ACMsg();
        req.setName("listMessageDataPoints");
        req.put("code", code);
        AC.sendToService(subDomain, "zc-product", 1, req, new PayloadCallback<ACMsg>() {
            @Override
            public void success(ACMsg resp) {
                LogUtil.d("test1", resp.toString());
                canSend = true;
                List<ACObject> dataPoints = resp.get("datapoints");
                if (dataPoints == null || dataPoints.size() == 0) {
                    adapter.dataPoints.clear();
                }
                adapter.dataPoints = dataPoints;
                adapter.notifyDataSetChanged();
            }

            @Override
            public void error(ACException e) {

            }
        });
    }

    class MyAdapter extends BaseAdapter {
        public List<ACObject> dataPoints;
        private Context context;

        public MyAdapter(Context context) {
            this.context = context;
            dataPoints = new ArrayList<ACObject>();
        }

        @Override
        public int getCount() {
            return dataPoints.size();
        }

        @Override
        public ACObject getItem(int i) {
            return dataPoints.get(i);
        }

        public String getDataName(int i) {
            return getItem(i).get("name");
        }

        public String getDataType(int i) {
            return getItem(i).get("data_type");
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            view = LayoutInflater.from(context).inflate(R.layout.adapter_data_points, null);
            TextView adapter_point_name = ViewHolder.get(view, R.id.adapter_point_name);
            TextView adapter_point_key = ViewHolder.get(view, R.id.adapter_point_key);
            EditText adapter_point_value = ViewHolder.get(view, R.id.adapter_point_value);

            ACObject dataPoint = getItem(i);
            String display_name = dataPoint.get("display_name");
            String name = dataPoint.get("name");
            String data_type = dataPoint.get("data_type");
            adapter_point_name.setText(display_name);
            adapter_point_key.setText(name);
            adapter_point_value.setText(editMap.get(i));
            adapter_point_value.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    editMap.put(i, s.toString());
                }
            });
            switch (data_type) {
                case "bool":
                    adapter_point_value.setHint("true/false");
                    break;
                case "int8":
                    adapter_point_value.setHint("int8");
                    adapter_point_value.setInputType(InputType.TYPE_CLASS_PHONE);
                    break;
                case "int16":
                    adapter_point_value.setHint("int16");
                    adapter_point_value.setInputType(InputType.TYPE_CLASS_PHONE);
                    break;
                case "int32":
                    adapter_point_value.setHint("int32");
                    adapter_point_value.setInputType(InputType.TYPE_CLASS_PHONE);
                    break;
                case "int64":
                    adapter_point_value.setHint("int64");
                    adapter_point_value.setInputType(InputType.TYPE_CLASS_PHONE);
                    break;
                case "float32":
                    adapter_point_value.setHint("float32");
                    adapter_point_value.setInputType(InputType.TYPE_CLASS_PHONE);
                    break;
                case "float64":
                    adapter_point_value.setHint("float64");
                    adapter_point_value.setInputType(InputType.TYPE_CLASS_PHONE);
                    break;
                case "string":
                    adapter_point_value.setHint("string");
                    break;
                case "binary":
                    adapter_point_value.setHint("unsupported binary here");
                    break;
                default:
                    adapter_point_value.setHint("unknown value type");
                    break;
            }
            return view;
        }
    }
}
