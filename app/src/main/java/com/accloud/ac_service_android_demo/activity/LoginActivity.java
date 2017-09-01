package com.accloud.ac_service_android_demo.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.accloud.ac_service_android_demo.R;
import com.accloud.ac_service_android_demo.utils.Pop;
import com.accloud.cloudservice.AC;
import com.accloud.cloudservice.PayloadCallback;
import com.accloud.service.ACAccountMgr;
import com.accloud.service.ACException;
import com.accloud.service.ACUserInfo;
import com.accloud.utils.PreferencesUtils;

/**
 * 登录页面
 * <p>
 * Created by sudongsheng on 2015/1/27.
 */
public class LoginActivity extends Activity implements View.OnClickListener {

    private EditText editTel;
    private EditText editPwd;
    private TextView register;
    private Button loginBtn;

    private String tel;
    private String pwd;
    //账号管理器
    ACAccountMgr accountMgr;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        editTel = (EditText) findViewById(R.id.login_edit_tel);
        editPwd = (EditText) findViewById(R.id.login_edit_pwd);
        register = (TextView) findViewById(R.id.register);
        loginBtn = (Button) findViewById(R.id.login);

        register.setOnClickListener(this);
        loginBtn.setOnClickListener(this);

        //通过AC获取账号管理器
        accountMgr = AC.accountMgr();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (accountMgr.isLogin()) {
            Intent intent = new Intent(LoginActivity.this, DeviceListActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.register:
                intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                break;
            case R.id.login:
                tel = editTel.getText().toString();
                pwd = editPwd.getText().toString();
                if (tel.length() == 0 || pwd.length() == 0) {
                    Pop.popToast(LoginActivity.this, getString(R.string.login_aty_username_or_pwd_cannot_be_empty));
                } else
                    login();
                break;
        }
    }

    public void login() {
        /**
         * @param email或telephone
         * @param password
         * @param callback<userId>
         */
        accountMgr.login(tel, pwd, new PayloadCallback<ACUserInfo>() {
            @Override
            public void success(ACUserInfo userInfo) {
                PreferencesUtils.putLong(LoginActivity.this, "userId", userInfo.getUserId());
                Intent intent = new Intent(LoginActivity.this, DeviceListActivity.class);
                startActivity(intent);
            }

            @Override
            public void error(ACException e) {
                Pop.popToast(LoginActivity.this, e.getErrorCode() + "-->" + e.getMessage());
            }
        });
    }
}
