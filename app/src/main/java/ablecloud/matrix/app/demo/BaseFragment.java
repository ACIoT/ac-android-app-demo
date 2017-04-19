package ablecloud.matrix.app.demo;

import android.app.Activity;
import android.app.Fragment;

import javax.inject.Inject;

import ablecloud.matrix.app.DeviceManager;

/**
 * Created by wangkun on 18/04/2017.
 */

public class BaseFragment extends Fragment {
    @Inject
    DeviceManager deviceManager;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((BaseActivity) getActivity()).getActivityComponent().inject(this);
    }
}
