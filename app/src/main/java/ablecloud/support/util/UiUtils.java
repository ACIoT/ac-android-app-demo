package ablecloud.support.util;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.widget.Toast;

import ablecloud.support.app.ProgressDialogFragment;

/**
 * Created by wangkun on 10/20/2016.
 */

public class UiUtils {
    public static void showToast(Context context, int textId) {
        Toast.makeText(context, textId, Toast.LENGTH_SHORT).show();
    }

    public static void showToast(Context context, CharSequence text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    public static void showProgressDialog(Activity activity, boolean show) {
        FragmentManager fm = activity.getFragmentManager();
        ProgressDialogFragment progress = (ProgressDialogFragment) fm.findFragmentByTag(ProgressDialogFragment.TAG);
        if (progress == null) {
            progress = ProgressDialogFragment.newInstance();
        }
        if (show) {
            progress.show(fm, ProgressDialogFragment.TAG);
        } else {
            progress.dismiss();
        }
    }
}
