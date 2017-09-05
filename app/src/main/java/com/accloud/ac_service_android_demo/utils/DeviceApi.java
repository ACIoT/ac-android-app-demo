package com.accloud.ac_service_android_demo.utils;

import android.widget.Toast;

import com.accloud.ac_service_android_demo.R;
import com.accloud.ac_service_android_demo.config.Config;
import com.accloud.cloudservice.AC;
import com.accloud.cloudservice.PayloadCallback;
import com.accloud.cloudservice.VoidCallback;
import com.accloud.service.ACDeviceMsg;
import com.accloud.service.ACException;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class DeviceApi {

    private static final int LIGHT_MSG_CODE = 68;
    private static final int LIGHT_OPEN = 1;
    private static final int LIGHT_CLOSE = 0;

    public static void openLight(final String physicalDeviceId, final VoidCallback voidCallback) {
        io.reactivex.Observable
                .create(new ObservableOnSubscribe<ACDeviceMsg>() {
                    @Override
                    public void subscribe(final ObservableEmitter<ACDeviceMsg> emitter) throws Exception {
                        AC.bindMgr().sendToDeviceWithOption(Config.SUB_DOMAIN, physicalDeviceId, createDeviceMsg(LIGHT_OPEN), AC.LOCAL_FIRST, new PayloadCallback<ACDeviceMsg>() {
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
                            ToastUtil.show(AC.context, AC.context.getString(R.string.main_aty_openlight_success));
                            if (voidCallback != null) {
                                voidCallback.success();
                            }
                        } else {
                            ToastUtil.show(AC.context, AC.context.getString(R.string.main_aty_openlight_fail));
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        ACException exception = (ACException) throwable;
                        Toast.makeText(AC.context, exception.getErrorCode() + "-->" + exception.getMessage(), Toast.LENGTH_LONG).show();
                        if (voidCallback != null) {
                            voidCallback.error(exception);
                        }
                    }
                });

    }

    public static void closeLight(final String physicalDeviceId, final VoidCallback voidCallback) {
        io.reactivex.Observable
                .create(new ObservableOnSubscribe<ACDeviceMsg>() {
                    @Override
                    public void subscribe(final ObservableEmitter<ACDeviceMsg> emitter) throws Exception {
                        AC.bindMgr().sendToDeviceWithOption(Config.SUB_DOMAIN, physicalDeviceId, createDeviceMsg(LIGHT_CLOSE), AC.LOCAL_FIRST, new PayloadCallback<ACDeviceMsg>() {
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
                            ToastUtil.show(AC.context, AC.context.getString(R.string.main_aty_closelight_success));
                            if (voidCallback != null) {
                                voidCallback.success();
                            }
                        } else {
                            ToastUtil.show(AC.context, AC.context.getString(R.string.main_aty_closelight_fail));
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        ACException exception = (ACException) throwable;
                        Toast.makeText(AC.context, exception.getErrorCode() + "-->" + exception.getMessage(), Toast.LENGTH_LONG).show();
                        if (voidCallback != null) {
                            voidCallback.error(exception);
                        }
                    }
                });
    }

    private static ACDeviceMsg createDeviceMsg(int action) {
        return new ACDeviceMsg(LIGHT_MSG_CODE, new byte[]{(byte) action, 0, 0, 0});
    }

    private static boolean parseDeviceMsg(ACDeviceMsg msg) {
        byte[] bytes = msg.getContent();
        if (bytes != null) return bytes[0] == 1;
        return false;
    }
}
