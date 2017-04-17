package ablecloud.matrix.app.demo.util;

import android.databinding.BindingAdapter;
import android.widget.TextView;

import com.accloud.service.ACUserDevice;

import ablecloud.matrix.app.demo.R;

/**
 * Created by wangkun on 17/04/2017.
 */

public class BindingUtils {
    @BindingAdapter("bind:onlineStatus")
    public static void setOnlineStatus(TextView textView, int onlineStatus) {
        int statusRes;
        switch (onlineStatus) {
            default:
            case ACUserDevice.OFFLINE:
                statusRes = R.string.device_offline;
                break;
            case ACUserDevice.LOCAL_ONLINE:
                statusRes = R.string.device_local_online;
                break;
            case ACUserDevice.NETWORK_ONLINE:
                statusRes = R.string.device_cloud_online;
                break;
            case ACUserDevice.BOTH_ONLINE:
                statusRes = R.string.device_both_online;
                break;
        }
        textView.setText(statusRes);
    }
}
