package ablecloud.matrix.app.demo;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by wangkun on 18/04/2017.
 */

public class UnknownControlFragment extends Fragment {
    public static final String TAG = "UnknownControlFragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_unknown_control, container, false);
    }
}
