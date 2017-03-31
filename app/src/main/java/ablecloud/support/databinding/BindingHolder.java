package ablecloud.support.databinding;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by wangkun on 24/07/2017.
 */

public class BindingHolder<T extends ViewDataBinding> extends RecyclerView.ViewHolder {

    public T binding;

    private BindingHolder(View root) {
        super(root);
    }

    public static BindingHolder create(int layout, ViewGroup parent) {
        ViewDataBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), layout, parent, false);
        BindingHolder holder = new BindingHolder(binding.getRoot());
        holder.binding = binding;
        return holder;
    }
}