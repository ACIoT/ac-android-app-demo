package com.accloud.ac_service_android_demo.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Xuri on 2015/4/8.
 */
public class Pop {
    static Toast toast = null;

    public static void popToast(Context context, String title) {
        if (toast == null)
            toast = Toast.makeText(context, title, Toast.LENGTH_LONG);
        else
            toast.setText(title);
        toast.show();
    }
}
