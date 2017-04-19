package ablecloud.matrix.app.demo;

import android.app.Fragment;

/**
 * Created by wangkun on 18/04/2017.
 */

public enum DeviceControl {
    unknown(UnknownControlFragment.class, UnknownControlFragment.TAG),
    demo(LightControlFragment.class, LightControlFragment.TAG);

    public final Class<? extends Fragment> fragment;
    public final String fragmentTag;

    DeviceControl(Class<? extends Fragment> fragment, String fragmentTag) {
        this.fragment = fragment;
        this.fragmentTag = fragmentTag;
    }
}
