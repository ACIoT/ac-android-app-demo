package com.accloud.ac_service_android_demo.activity;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.accloud.cloudservice.ACDeviceActivator;

/**
 * Created by wangkun on 11/08/2017.
 */
public class ActivatorAdapter extends ArrayAdapter<ActivatorAdapter.Info> {
    public ActivatorAdapter(Context context) {
        super(context, 0);
        addAll(Info.values());
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        }
        ((TextView) convertView).setText(getItem(position).name());
        return convertView;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
        }
        ((TextView) convertView).setText(getItem(position).name());
        return convertView;
    }

    public enum Info {
        HF(ACDeviceActivator.HF),
        MX(ACDeviceActivator.MX),
        QCSNIFFER(ACDeviceActivator.QCSNIFFER),
        ESP8266(ACDeviceActivator.ESP8266),
        REALTEK(ACDeviceActivator.REALTEK),
        AI6060H(ACDeviceActivator.AI6060H),
        BL(ACDeviceActivator.BL),
        QCLTLINK(ACDeviceActivator.QCLTLINK);

        public final int type;

        Info(int type) {
            this.type = type;
        }
    }
}
