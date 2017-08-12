package com.accloud.ac_service_android_demo.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.accloud.ac_service_android_demo.R;
import com.accloud.ac_service_android_demo.config.Config;
import com.accloud.cloudservice.ACDeviceActivator;
import com.accloud.utils.PreferencesUtils;

/**
 * 查看配置文件
 * <p/>
 * Created by Xuri on 2015/1/29.
 */
public class ConfigDetailActivity extends Activity {
    TextView back;
    EditText editDomain;
    EditText editDomainId;
    EditText editSubDomain;
    Spinner typeSpinner;
    Button config;

    private int deviceType = 0;
    private ActivatorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_detail);
        back = (TextView) findViewById(R.id.config_back);
        editDomain = (EditText) findViewById(R.id.config_edit_domain);
        editDomainId = (EditText) findViewById(R.id.config_edit_id);
        editSubDomain = (EditText) findViewById(R.id.config_edit_subDomain);

        editDomain.setText(PreferencesUtils.getString(ConfigDetailActivity.this, "domain", Config.MAJORDOAMIN));
        editDomainId.setText("" + PreferencesUtils.getLong(ConfigDetailActivity.this, "domainId", Config.MAJORDOMAINID));
        editSubDomain.setText(PreferencesUtils.getString(ConfigDetailActivity.this, "subDomain", Config.SUBDOMAIN));

        adapter = new ActivatorAdapter(this);
        typeSpinner = (Spinner) findViewById(R.id.config_type);
        typeSpinner.setAdapter(adapter);
        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                deviceType = adapter.getItem(position).type;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        int type = PreferencesUtils.getInt(this, "deviceType", ACDeviceActivator.HF);
        ActivatorAdapter.Info[] infos = ActivatorAdapter.Info.values();
        for (int i = 0; i < infos.length; i++) {
            if (infos[i].type == type) {
                typeSpinner.setSelection(i);
            }
        }
        config = (Button) findViewById(R.id.config);
        config.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PreferencesUtils.putInt(ConfigDetailActivity.this, "deviceType", deviceType);

                ConfigDetailActivity.this.finish();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
