package ablecloud.matrix.app.demo;

import dagger.Module;

/**
 * Created by wangkun on 25/02/2017.
 */

@Module
public class ActivityModule {
    BaseActivity activity;

    public ActivityModule(BaseActivity baseActivity) {
        this.activity = baseActivity;
    }
}
