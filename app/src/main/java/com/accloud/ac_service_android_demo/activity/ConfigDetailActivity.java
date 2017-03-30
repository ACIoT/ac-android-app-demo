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

import com.accloud.ac_service_android_demo.R;
import com.accloud.ac_service_android_demo.config.Config;
import com.accloud.cloudservice.AC;
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

        typeSpinner = (Spinner) findViewById(R.id.config_type);
        typeSpinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, new String[]{"HF", "MTK", "QCA4004", "MX", "MARATA", "WM", "MARVELL", "RAK", "TI", "ESP8266", "REALTEK", "AI6060H", "MILL", "GUBEI", "COOEE"}));
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
                deviceType = PreferencesUtils.getInt(ConfigDetailActivity.this, "deviceType", AC.DEVICE_HF);
            }
        });
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
