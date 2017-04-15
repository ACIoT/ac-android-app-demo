package ablecloud.support.util;

import android.content.Context;

import com.accloud.service.ACException;

import ablecloud.matrix.app.demo.R;

/**
 * Created by wangkun on 15/04/2017.
 */

public class Utils {
    public static void showError(Context context, ACException e) {
        String text = context.getString(R.string.error_unknown, e.getErrorCode(), e.getMessage());
        UiUtils.showToast(context, text);
    }
}
