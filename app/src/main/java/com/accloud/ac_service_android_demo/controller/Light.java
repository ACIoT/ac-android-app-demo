package com.accloud.ac_service_android_demo.controller;

import android.content.Context;
import android.widget.Toast;

import com.accloud.ac_service_android_demo.R;
import com.accloud.ac_service_android_demo.config.Config;
import com.accloud.ac_service_android_demo.utils.Pop;
import com.accloud.cloudservice.AC;
import com.accloud.cloudservice.PayloadCallback;
import com.accloud.cloudservice.VoidCallback;
import com.accloud.service.ACDeviceMsg;
import com.accloud.service.ACException;
import com.accloud.utils.PreferencesUtils;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Xuri on 2015/1/24.
 */
public class Light {
    private static final int OPENLIGHT = 1;
    private static final int CLOSELIGHT = 0;
    private Context context;
    private String subDomain;

    public Light(Context context) {
        this.context = context;
        subDomain = PreferencesUtils.getString(context, "subDomain", Config.SUB_DOMAIN);
    }

    public void openLight(final String physicalDeviceId, final VoidCallback voidCallback) {
        io.reactivex.Observable
                .create(new ObservableOnSubscribe<ACDeviceMsg>() {
                    @Override
                    public void subscribe(final ObservableEmitter<ACDeviceMsg> emitter) throws Exception {
                        AC.bindMgr().sendToDeviceWithOption(subDomain, physicalDeviceId, getDeviceMsg(OPENLIGHT), AC.LOCAL_FIRST, new PayloadCallback<ACDeviceMsg>() {
                            @Override
                            public void success(ACDeviceMsg msg) {
                                emitter.onNext(msg);
                            }

                            @Override
                            public void error(ACException e) {
                                emitter.onError(e);
                            }
                        });
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ACDeviceMsg>() {
                    @Override
                    public void accept(@NonNull ACDeviceMsg acDeviceMsg) throws Exception {
                        if (parseDeviceMsg(acDeviceMsg)) {
                            Pop.popToast(context, context.getString(R.string.main_aty_openlight_success));
                            if (voidCallback != null) {
                                voidCallback.success();
                            }
                        } else {
                            Pop.popToast(context, context.getString(R.string.main_aty_openlight_fail));
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        ACException exception = (ACException) throwable;
                        Toast.makeText(context, exception.getErrorCode() + "-->" + exception.getMessage(), Toast.LENGTH_LONG).show();
                        if (voidCallback != null) {
                            voidCallback.error(exception);
                        }
                    }
                });

    }

    public void closeLight(final String physicalDeviceId, final VoidCallback voidCallback) {
        io.reactivex.Observable
                .create(new ObservableOnSubscribe<ACDeviceMsg>() {
                    @Override
                    public void subscribe(final ObservableEmitter<ACDeviceMsg> emitter) throws Exception {
                        AC.bindMgr().sendToDeviceWithOption(subDomain, physicalDeviceId, getDeviceMsg(CLOSELIGHT), AC.LOCAL_FIRST, new PayloadCallback<ACDeviceMsg>() {
                            @Override
                            public void success(ACDeviceMsg msg) {
                                emitter.onNext(msg);
                            }

                            @Override
                            public void error(ACException e) {
                                emitter.onError(e);
                            }
                        });
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ACDeviceMsg>() {
                    @Override
                    public void accept(@NonNull ACDeviceMsg acDeviceMsg) throws Exception {
                        if (parseDeviceMsg(acDeviceMsg)) {
                            Pop.popToast(context, context.getString(R.string.main_aty_closelight_success));
                            if (voidCallback != null) {
                                voidCallback.success();
                            }
                        } else {
                            Pop.popToast(context, context.getString(R.string.main_aty_closelight_fail));
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        ACException exception = (ACException) throwable;
                        Toast.makeText(context, exception.getErrorCode() + "-->" + exception.getMessage(), Toast.LENGTH_LONG).show();
                        if (voidCallback != null) {
                            voidCallback.error(exception);
                        }
                    }
                });
    }

    private ACDeviceMsg getDeviceMsg(int action) {
        return new ACDeviceMsg(Config.LIGHT_MSGCODE, new byte[]{(byte) action, 0, 0, 0});
    }

    private boolean parseDeviceMsg(ACDeviceMsg msg) {
        byte[] bytes = msg.getContent();
        if (bytes != null) return bytes[0] == 1;
        return false;
    }
}
