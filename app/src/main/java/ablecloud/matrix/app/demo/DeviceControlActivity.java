package ablecloud.matrix.app.demo;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.accloud.service.ACUserDevice;

import ablecloud.matrix.app.demo.util.IntentExtra;

/**
 * Created by wangkun on 18/04/2017.
 */

public class DeviceControlActivity extends BaseFragmentActivity {

    private Toolbar toolbar;
    private TextView title;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        title = (TextView) findViewById(android.R.id.title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP, ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE);

        long deviceId = getIntent().getLongExtra(IntentExtra.DEVICE_ID, 0);
        ACUserDevice device = deviceManager.findDevice(deviceId);
        DeviceControl deviceControl = device != null ? DeviceControl.valueOf(device.subDomain) : DeviceControl.unknown;
        Fragment fragment = Fragment.instantiate(this, deviceControl.fragment.getName());
        fragment.setArguments(getIntent().getExtras());
        addFragment(fragment, deviceControl.fragmentTag);
    }

    @Override
    protected int getContentLayoutRes() {
        return R.layout.activity_device_control;
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
        this.title.setText(title);
    }
}
