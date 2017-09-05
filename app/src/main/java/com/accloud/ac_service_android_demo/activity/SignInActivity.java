package com.accloud.ac_service_android_demo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.accloud.ac_service_android_demo.R;
import com.accloud.ac_service_android_demo.utils.ToastUtil;
import com.accloud.cloudservice.AC;
import com.accloud.cloudservice.PayloadCallback;
import com.accloud.service.ACException;
import com.accloud.service.ACUserInfo;
import com.accloud.utils.PreferencesUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by liuxiaofeng on 05/09/2017.
 */

public class SignInActivity extends AppCompatActivity {

    @BindView(R.id.account)
    EditText account;
    @BindView(R.id.password)
    EditText password;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        ButterKnife.bind(this);
        getSupportActionBar().setTitle(getString(R.string.login_aty_title));
        if (AC.accountMgr().isLogin()) {
            startActivity(new Intent(this, DeviceListActivity.class));
        }
    }

    @OnClick({R.id.signIn, R.id.signUp})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.signIn:
                signIn();
                break;
            case R.id.signUp:
                signUp();
                break;
        }
    }

    private void signIn() {
        String account = this.account.getText().toString().trim();
        String password = this.password.getText().toString().trim();
        if (TextUtils.isEmpty(account)) {
            ToastUtil.show(this, "账号不能为空");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            ToastUtil.show(this, "密码不能为空");
            return;
        }
        AC.accountMgr().login(account, password, new PayloadCallback<ACUserInfo>() {
            @Override
            public void success(ACUserInfo acUserInfo) {
                PreferencesUtils.putLong(SignInActivity.this, "userId", acUserInfo.getUserId());
                Intent intent = new Intent(SignInActivity.this, DeviceListActivity.class);
                startActivity(intent);
            }

            @Override
            public void error(ACException e) {
                ToastUtil.show(SignInActivity.this, e.getErrorCode() + "-->" + e.getMessage());
            }
        });
    }

    private void signUp() {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }
}
