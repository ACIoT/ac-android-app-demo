package ablecloud.matrix.app.demo;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.accloud.service.ACUserDevice;

import ablecloud.matrix.app.demo.databinding.FragmentLightControlBinding;
import ablecloud.matrix.app.demo.util.IntentExtra;

/**
 * Created by wangkun on 18/04/2017.
 */

public class LightControlFragment extends BaseFragment {
    public static final String TAG = "LightControlFragment";

    private FragmentLightControlBinding binding;
    private ACUserDevice device;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int deviceId = getArguments().getInt(IntentExtra.DEVICE_ID, 0);
        device = deviceManager.findDevice(deviceId);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_light_control, container, false);
        binding.setDevice(device);
        return binding.getRoot();
    }
}
