package ablecloud.matrix.app.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import ablecloud.matrix.MatrixCallback;
import ablecloud.matrix.MatrixError;
import ablecloud.matrix.app.Matrix;
import ablecloud.matrix.model.User;
import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;

/**
 * Created by wangkun on 24/07/2017.
 */

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.local_device_list:
                forward(LocalDeviceListActivity.class);
                break;
            case R.id.cloud_device_list:
                ensureLogin(new Action() {
                    @Override
                    public void run() throws Exception {
                        forward(DeviceListActivity.class);
                    }
                });
                break;
        }
    }

    private void ensureLogin(Action onComplete) {
        boolean login = Matrix.accountManager().isLogin();
        Completable completable = login ? Completable.complete() : Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(final CompletableEmitter emitter) throws Exception {
                Matrix.accountManager().login(/*account*/"", /*password*/"", new MatrixCallback<User>() {
                    @Override
                    public void success(User user) {
                        emitter.onComplete();
                    }

                    @Override
                    public void error(MatrixError error) {
                        emitter.onError(error);
                    }
                });
            }
        });
        completable.observeOn(AndroidSchedulers.mainThread()).subscribe(onComplete);
    }

    private void forward(Class<? extends Activity> activityClass) {
        startActivity(new Intent(this, activityClass));
    }
}
