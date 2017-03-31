package ablecloud.support.databinding;

import android.database.DataSetObserver;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.RecyclerView;

/**
 * Created by wangkun on 28/02/2017.
 */
public abstract class CountObservable extends BaseObservable {

    @Bindable
    public abstract int getCount();

    public static CountObservable create(RecyclerView.Adapter adapter) {
        return new RecyclerViewObservable(adapter);
    }

    public static CountObservable create(PagerAdapter adapter) {
        return new PagerObservable(adapter);
    }

    private static class PagerObservable extends CountObservable {
        private PagerAdapter adapter;

        private PagerObservable(PagerAdapter adapter) {
            this.adapter = adapter;
            adapter.registerDataSetObserver(new DataSetObserver() {
                @Override
                public void onChanged() {
                    notifyChange();
                }
            });
        }

        @Override
        public int getCount() {
            return adapter.getCount();
        }
    }

    private static class RecyclerViewObservable extends CountObservable {
        private RecyclerView.Adapter adapter;

        public RecyclerViewObservable(RecyclerView.Adapter adapter) {
            this.adapter = adapter;
            adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                @Override
                public void onChanged() {
                    notifyChange();
                }

                @Override
                public void onItemRangeInserted(int positionStart, int itemCount) {
                    notifyChange();
                }

                @Override
                public void onItemRangeRemoved(int positionStart, int itemCount) {
                    notifyChange();
                }
            });
        }

        @Override
        public int getCount() {
            return adapter.getItemCount();
        }
    }
}
