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

        typeSpinner = (Spinner) findViewById(R.id.config_type);
        formatSpinner = (Spinner) findViewById(R.id.config_format);
        typeSpinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, new String[]{"HF", "MTK", "QCA4004", "MX", "MARATA", "WM", "MARVELL", "RAK", "TI", "ESP8266", "REALTEK", "AI6060H", "MILL", "GUBEI", "COOEE"}));
        formatSpinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, new String[]{"BINARY", "JSON"}));
        switch (PreferencesUtils.getInt(this, "deviceType", AC.DEVICE_HF)) {
            case AC.DEVICE_HF:
                typeSpinner.setSelection(0);
                break;
            case AC.DEVICE_MTK:
                typeSpinner.setSelection(1);
                break;
            case AC.DEVICE_QCA4004:
                typeSpinner.setSelection(2);
                break;
            case AC.DEVICE_MX:
                typeSpinner.setSelection(3);
                break;
            case AC.DEVICE_MURATA:
                typeSpinner.setSelection(4);
                break;
            case AC.DEVICE_WM:
                typeSpinner.setSelection(5);
                break;
            case AC.DEVICE_MARVELL:
                typeSpinner.setSelection(6);
                break;
            case AC.DEVICE_RAK:
                typeSpinner.setSelection(7);
                break;
            case AC.DEVICE_TI:
                typeSpinner.setSelection(8);
                break;
            case AC.DEVICE_ESP8266:
                typeSpinner.setSelection(9);
                break;
            case AC.DEVICE_REALTEK:
                typeSpinner.setSelection(10);
                break;
            case AC.DEVICE_AI6060H:
                typeSpinner.setSelection(11);
                break;
            case AC.DEVICE_MILL:
                typeSpinner.setSelection(12);
                break;
            case AC.DEVICE_GUBEI:
                typeSpinner.setSelection(13);
                break;
            case AC.DEVICE_COOEE:
                typeSpinner.setSelection(14);
                break;
        }
        switch (PreferencesUtils.getInt(this, "formatType", BINARY)) {
            case BINARY:
                formatSpinner.setSelection(BINARY);
                break;
            case JSON:
                formatSpinner.setSelection(JSON);
                break;
        }
        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0)
                    deviceType = AC.DEVICE_HF;
                else if (i == 1)
                    deviceType = AC.DEVICE_MTK;
                else if (i == 2)
                    deviceType = AC.DEVICE_QCA4004;
                else if (i == 3)
                    deviceType = AC.DEVICE_MX;
                else if (i == 4)
                    deviceType = AC.DEVICE_MURATA;
                else if (i == 5)
                    deviceType = AC.DEVICE_WM;
                else if (i == 6)
                    deviceType = AC.DEVICE_MARVELL;
                else if (i == 7)
                    deviceType = AC.DEVICE_RAK;
                else if (i == 8)
                    deviceType = AC.DEVICE_TI;
                else if (i == 9)
                    deviceType = AC.DEVICE_ESP8266;
                else if (i == 10)
                    deviceType = AC.DEVICE_REALTEK;
                else if (i == 11)
                    deviceType = AC.DEVICE_AI6060H;
                else if (i == 12)
                    deviceType = AC.DEVICE_MILL;
                else if (i == 13)
                    deviceType = AC.DEVICE_GUBEI;
                else if (i == 14)
                    deviceType = AC.DEVICE_COOEE;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                deviceType = PreferencesUtils.getInt(ConfigurationActivity.this, "deviceType", AC.DEVICE_HF);
            }
        });
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
