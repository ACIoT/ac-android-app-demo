package com.accloud.ac_service_android_demo.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.accloud.ac_service_android_demo.R;
import com.accloud.ac_service_android_demo.activity.DeviceListActivity;
import com.accloud.ac_service_android_demo.utils.ToastUtil;
import com.accloud.cloudservice.AC;
import com.accloud.cloudservice.PayloadCallback;
import com.accloud.service.ACException;
import com.accloud.service.ACUserInfo;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by liuxiaofeng on 05/09/2017.
 */

public class SmsVerifyFragment extends Fragment {

    @BindView(R.id.password)
    EditText password;
    @BindView(R.id.passwordConfirm)
    EditText passwordConfirm;
    @BindView(R.id.verifyCode)
    EditText verifyCode;

    public static final String TAG = "SmsVerifyFragment";
    Unbinder unbinder;
    private String phone;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        phone = getArguments().getString("phone");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sms_verify, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.signUp)
    public void onViewClicked() {
        String password = this.password.getText().toString().trim();
        String passwordConfirm = this.passwordConfirm.getText().toString().trim();
        String verifyCode = this.verifyCode.getText().toString().trim();
        if (TextUtils.isEmpty(password) || TextUtils.isEmpty(passwordConfirm) || TextUtils.isEmpty(verifyCode)) {
            ToastUtil.show(getActivity(), "密码、密码确认、验证码均不能为空");
            return;
        }
        if (!password.equals(passwordConfirm)) {
            ToastUtil.show(getActivity(), "两次输入的密码不一致");
            return;
        }
        AC.accountMgr().register("", phone, password, phone, verifyCode, new PayloadCallback<ACUserInfo>() {
            @Override
            public void success(ACUserInfo acUserInfo) {
                startActivity(new Intent(getActivity(), DeviceListActivity.class));
                getActivity().finish();
            }

            @Override
            public void error(ACException e) {
                ToastUtil.show(getActivity(), e.toString());
            }
        });
    }
}
