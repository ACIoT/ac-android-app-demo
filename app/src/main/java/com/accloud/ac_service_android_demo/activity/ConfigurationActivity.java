package com.accloud.ac_service_android_demo.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.accloud.ac_service_android_demo.R;
import com.accloud.ac_service_android_demo.application.MainApplication;
import com.accloud.cloudservice.AC;
import com.accloud.cloudservice.ACDeviceActivator;
import com.accloud.utils.PreferencesUtils;

/**
 * 配置文件
 * <p/>
 * Created by Xuri on 2015/1/29.
 */
public class ConfigurationActivity extends Activity {
    public static final int BINARY = 0;
    public static final int JSON = 1;

    TextView back;
    EditText editDomain;
    EditText editDomainId;
    EditText editRouter;
    EditText editSubDomain;
    Spinner typeSpinner;
    Spinner formatSpinner;
    Button config;

    private int deviceType = 0;
    private int formatType = 0;

    private ActivatorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);
        back = (TextView) findViewById(R.id.config_back);
        editDomain = (EditText) findViewById(R.id.config_edit_domain);
        editDomainId = (EditText) findViewById(R.id.config_edit_id);
        editSubDomain = (EditText) findViewById(R.id.config_edit_subDomain);
        editRouter = (EditText) findViewById(R.id.config_edit_router);

        editDomain.setText(PreferencesUtils.getString(ConfigurationActivity.this, "domain", ""));
        long domainId = PreferencesUtils.getLong(ConfigurationActivity.this, "domainId", 0L);
        editDomainId.setText(domainId == 0L ? "" : domainId + "");
        editSubDomain.setText(PreferencesUtils.getString(ConfigurationActivity.this, "subDomain", ""));
        editRouter.setText(PreferencesUtils.getString(ConfigurationActivity.this, "routerAddr", ""));

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

        formatSpinner = (Spinner) findViewById(R.id.config_format);
        formatSpinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, new String[]{"BINARY", "JSON"}));
        switch (PreferencesUtils.getInt(this, "formatType", BINARY)) {
            case BINARY:
                formatSpinner.setSelection(BINARY);
                break;
            case JSON:
                formatSpinner.setSelection(JSON);
                break;
        }
        formatSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0)
                    formatType = BINARY;
                else
                    formatType = JSON;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                formatType = PreferencesUtils.getInt(ConfigurationActivity.this, "formatType", BINARY);
            }
        });

        config = (Button) findViewById(R.id.config);
        config.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String domain = editDomain.getText().toString();
                String domainId = editDomainId.getText().toString();
                String subDomain = editSubDomain.getText().toString();
                if (domain.length() > 0 && domainId.length() > 0 && subDomain.length() > 0) {
                    PreferencesUtils.putString(ConfigurationActivity.this, "domain", domain);
                    PreferencesUtils.putString(ConfigurationActivity.this, "subDomain", subDomain);
                    long id = 0;
                    try {
                        id = Long.parseLong(editDomainId.getText().toString());
                        PreferencesUtils.putLong(ConfigurationActivity.this, "domainId", id);
                    } catch (Exception e) {
                        Toast.makeText(ConfigurationActivity.this, getString(R.string.configuration_aty_domainid_error_toast_hint), Toast.LENGTH_LONG).show();
                        return;
                    }
                    AC.init(MainApplication.getInstance(), domain, id, AC.TEST_MODE);
                }

                String router = editRouter.getText().toString();
                if (router.length() > 0) {
                    if (router.contains(":"))
                        router = router.substring(0, router.indexOf(":"));
                    AC.setRouterAddress(router);
                    PreferencesUtils.putString(ConfigurationActivity.this, "routerAddr", router);
                }

                PreferencesUtils.putInt(ConfigurationActivity.this, "formatType", formatType);
                PreferencesUtils.putInt(ConfigurationActivity.this, "deviceType", deviceType);

                Toast.makeText(ConfigurationActivity.this, getString(R.string.configuration_aty_configsuccess), Toast.LENGTH_LONG).show();
                ConfigurationActivity.this.finish();
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
