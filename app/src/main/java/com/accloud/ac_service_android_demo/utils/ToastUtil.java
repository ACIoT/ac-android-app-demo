package com.accloud.ac_service_android_demo.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {

    public static void show(Context context, String title) {
        Toast.makeText(context, title, Toast.LENGTH_SHORT).show();
    }
}
