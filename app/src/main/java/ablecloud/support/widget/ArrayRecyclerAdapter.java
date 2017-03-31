package ablecloud.support.widget;

import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Created by wangkun on 10/28/16.
 */
public abstract class ArrayRecyclerAdapter<E, H extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<H> {

    protected List<E> mItems = new ArrayList<>();

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public E getItem(int position) {
        return mItems.get(position);
    }

    public void add(E item) {
        mItems.add(item);
        notifyItemInserted(mItems.size());
    }

    public void addAll(E[] items) {
        mItems.addAll(Arrays.asList(items));
    }

    public void addAll(Collection<E> items) {
        mItems.addAll(items);
        notifyItemRangeInserted(mItems.size(), items.size());
    }

    public void remove(E item) {
        for (int i = 0; i < mItems.size(); i++) {
            if (mItems.get(i) == item) {
                mItems.remove(i);
                notifyItemRemoved(i);
                return;
            }
        }
    }

    public void clear() {
        int size = mItems.size();
        mItems.clear();
        notifyItemRangeRemoved(0, size);
    }
}
