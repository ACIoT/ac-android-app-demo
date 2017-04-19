package ablecloud.matrix.app.demo;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MenuItem;

/**
 * Created by wangkun on 18/04/2017.
 */

public class BaseFragmentActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentLayoutRes());
    }

    protected int getContentLayoutRes() {
        return R.layout.activity_content_panel;
    }

    protected int getContentPanelId() {
        return R.id.contentPanel;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (!super.onOptionsItemSelected(item)) {
                onBackPressed();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void addFragment(Fragment fragment) {
        addFragment(fragment, null);
    }

    public void addFragment(Fragment fragment, String tag) {
        addFragment(fragment, tag, false);
    }

    public void addFragment(Fragment fragment, String tag, boolean addToStack) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        if (tag != null) {
            transaction.add(getContentPanelId(), fragment, tag);
        } else {
            transaction.add(getContentPanelId(), fragment);
        }
        if (addToStack) transaction.addToBackStack(tag);
        transaction.commitAllowingStateLoss();
    }

    public void replaceFragment(Fragment fragment) {
        replaceFragment(fragment, null);
    }

    public void replaceFragment(Fragment fragment, String tag) {
        replaceFragment(fragment, tag, false);
    }

    public void replaceFragment(Fragment fragment, String tag, boolean addToStack) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        if (tag != null) {
            transaction.replace(getContentPanelId(), fragment, tag);
        } else {
            transaction.replace(getContentPanelId(), fragment);
        }
        if (addToStack) transaction.addToBackStack(tag);
        transaction.commitAllowingStateLoss();
    }
}
