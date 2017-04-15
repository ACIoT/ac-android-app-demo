package ablecloud.support.app;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;

/**
 * Created by wangkun on 12/15/2016.
 */
public class ProgressDialogFragment extends DialogFragment {

    public static final String TAG = "ProgressDialogFragment";

    private static final long MIN_DURATION_MS = 1000;

    private Handler mHandler;

    private Runnable mDismissCallback = new Runnable() {
        @Override
        public void run() {
            ProgressDialogFragment.super.dismiss();
        }
    };

    public ProgressDialogFragment() {
    }

    public static ProgressDialogFragment newInstance() {
        return new ProgressDialogFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new Handler();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(mDismissCallback);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new ProgressDialog(getActivity());
    }

    @Override
    public void dismiss() {
        mHandler.postDelayed(mDismissCallback, MIN_DURATION_MS);
    }
}
