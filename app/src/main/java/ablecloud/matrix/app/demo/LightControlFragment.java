package ablecloud.matrix.app.demo;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.accloud.cloudservice.PayloadCallback;
import com.accloud.service.ACException;
import com.accloud.service.ACUserDevice;

import ablecloud.matrix.app.demo.databinding.FragmentLightControlBinding;
import ablecloud.matrix.app.demo.util.IntentExtra;
import ablecloud.support.util.UiUtils;

/**
 * Created by wangkun on 18/04/2017.
 */

public class LightControlFragment extends BaseFragment implements View.OnClickListener {
    public static final String TAG = "LightControlFragment";

    private FragmentLightControlBinding binding;
    private ACUserDevice device;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        long deviceId = getArguments().getLong(IntentExtra.DEVICE_ID, 0);
        device = deviceManager.findDevice(deviceId);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_light_control, container, false);
        binding.setDevice(device);
        binding.powerButton.setOnClickListener(this);
        return binding.getRoot();
    }

    @Override
    public void onClick(final View v) {
        if (v == binding.powerButton) {
            final boolean powerOn = !binding.light.isActivated();
            LightDeviceApi.turnPower(device.getPhysicalDeviceId(), powerOn, new PayloadCallback<Boolean>() {
                @Override
                public void success(Boolean aBoolean) {
                    if (aBoolean) {
                        UiUtils.showToast(getActivity(), powerOn ? R.string.light_on_success : R.string.light_off_success);
                        binding.setPowerStatus(powerOn);
                    } else {
                        UiUtils.showToast(getActivity(), R.string.light_no_response);
                    }
                }

                @Override
                public void error(ACException e) {
                    Log.d(TAG, e.getMessage());
                    UiUtils.showToast(getActivity(), e.getMessage());
                }
            });
        }
    }
}
