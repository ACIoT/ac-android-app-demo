package com.accloud.ac_service_android_demo.utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.accloud.ac_service_android_demo.R;

/**
 * Created by liuxiaofeng on 26/05/2017.
 */

public class FragmentUtil {

    public static void replaceSupportFragment(AppCompatActivity activity, int containerId, Class<? extends Fragment> fragmentClass, String tag, boolean addToBackStack, boolean haveAnim) {
        try {
            FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
            if (addToBackStack) {
                transaction.addToBackStack(null);
            }
            if (haveAnim) {
                transaction.setCustomAnimations(R.anim.backstack_push_enter, R.anim.backstack_push_exit, R.anim.backstack_pop_enter, R.anim.backstack_pop_exit);
            }
            transaction.replace(containerId, fragmentClass.newInstance(), tag);
            transaction.commitAllowingStateLoss();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static void replaceSupportFragment(AppCompatActivity activity, int containerId, Fragment fragment, String tag, boolean addToBackStack, boolean haveAnim) {
        FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        if (haveAnim) {
            transaction.setCustomAnimations(R.anim.backstack_push_enter, R.anim.backstack_push_exit, R.anim.backstack_pop_enter, R.anim.backstack_pop_exit);
        }
        transaction.replace(containerId, fragment);
        transaction.commitAllowingStateLoss();
    }


}
