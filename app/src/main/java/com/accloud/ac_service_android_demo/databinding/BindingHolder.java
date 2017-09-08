package com.accloud.ac_service_android_demo.databinding;

import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;

/**
 * Created by liuxiaofeng on 06/09/2017.
 */

public class BindingHolder<T extends ViewDataBinding> extends RecyclerView.ViewHolder {

    public T binding;

    public BindingHolder(T binding) {
        super(binding.getRoot());
        this.binding = binding;
    }
}
