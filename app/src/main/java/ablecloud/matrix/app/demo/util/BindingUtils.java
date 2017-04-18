package ablecloud.matrix.app.demo.util;

import android.databinding.BindingAdapter;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.TextView;

import com.accloud.service.ACUserDevice;
import com.squareup.picasso.Picasso;

import ablecloud.matrix.app.demo.R;

/**
 * Created by wangkun on 17/04/2017.
 */

public class BindingUtils {
    @BindingAdapter("bind:onlineStatus")
    public static void setOnlineStatus(TextView textView, int onlineStatus) {
        int statusRes;
        int colorAttr;
        switch (onlineStatus) {
            default:
            case ACUserDevice.OFFLINE:
                statusRes = R.string.device_offline;
                colorAttr = R.attr.status_offline;
                break;
            case ACUserDevice.LOCAL_ONLINE:
                statusRes = R.string.device_local_online;
                colorAttr = R.attr.status_local;
                break;
            case ACUserDevice.NETWORK_ONLINE:
                statusRes = R.string.device_cloud_online;
                colorAttr = R.attr.status_cloud;
                break;
            case ACUserDevice.BOTH_ONLINE:
                statusRes = R.string.device_both_online;
                colorAttr = R.attr.status_both;
                break;
        }
        textView.setText(statusRes);
        TypedValue outValue = new TypedValue();
        if (textView.getContext().getTheme().resolveAttribute(colorAttr, outValue, true)) {
            textView.setTextColor(outValue.data);
        }
    }

    @BindingAdapter("bind:imageUrl")
    public static void setImageUrl(ImageView imageView, String url) {
        if (url != null && url.length() > 0)
            Picasso.with(imageView.getContext()).load(url).placeholder(R.drawable.ic_product_default).into(imageView);
    }
}
