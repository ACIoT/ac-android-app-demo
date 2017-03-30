package com.accloud.ac_service_android_demo.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.accloud.ac_service_android_demo.R;
import com.accloud.ac_service_android_demo.utils.Pop;
import com.accloud.cloudservice.AC;
import com.accloud.cloudservice.PayloadCallback;
import com.accloud.cloudservice.VoidCallback;
import com.accloud.service.ACAccountMgr;
import com.accloud.service.ACException;
import com.accloud.service.ACUserInfo;

/**
 * 注册页面
 * <p>
 * Created by sudongsheng on 2015/1/27.
 */
public class RegisterActivity extends Activity implements View.OnClickListener {

    private EditText editPhone;
    private EditText editEmail;
    private EditText editName;
    private EditText editPwd;
    private EditText editRePwd;
    private EditText editVCode;
    private Button vCodeBtn;
    private Button registerBtn;
    private TextView back;

    String phone;
    String email;
    String name;
    String pwd;
    String vcode;

    //账号管理器
    ACAccountMgr accountMgr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        editPhone = (EditText) findViewById(R.id.register_edit_tel);
        editEmail = (EditText) findViewById(R.id.register_edit_email);
        editName = (EditText) findViewById(R.id.register_edit_name);
        editPwd = (EditText) findViewById(R.id.register_edit_pwd);
        editRePwd = (EditText) findViewById(R.id.register_edit_repwd);
        editRePwd = (EditText) findViewById(R.id.register_edit_repwd);
        editVCode = (EditText) findViewById(R.id.register_edit_vcode);
        vCodeBtn = (Button) findViewById(R.id.register_vcode);
        registerBtn = (Button) findViewById(R.id.register);
        back = (TextView) findViewById(R.id.register_back);

        //通过AC获取账号管理器
        accountMgr = AC.accountMgr();

        vCodeBtn.setOnClickListener(this);
        registerBtn.setOnClickListener(this);
        back.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.register_vcode:
                phone = editPhone.getText().toString();
                email = editEmail.getText().toString();
                String account = null;
                if (phone.length() > 0) {
                    account = phone;
                } else if (email.length() > 0) {
                    account = email;
                } else {
                    Pop.popToast(RegisterActivity.this, getString(R.string.register_aty_phone_or_email_cannot_be_empty));
                    return;
                }
                sendVerifyCode(account);
                break;
            case R.id.register:
                phone = editPhone.getText().toString();
                email = editEmail.getText().toString();
                pwd = editPwd.getText().toString();
                name = editName.getText().toString();
                String rePwd = editRePwd.getText().toString();
                vcode = editVCode.getText().toString();
                if (!pwd.equals(rePwd)) {
                    Pop.popToast(RegisterActivity.this, getString(R.string.register_aty_repeat_pwd_incorrect));
                }
                register();
                break;
            case R.id.register_back:
                RegisterActivity.this.finish();
                break;
        }
    }

    //发送验证码
    public void sendVerifyCode(String account) {
        /**
         * @param email或telephone
         * @param callback
         */
        accountMgr.sendVerifyCode(account, 1, new VoidCallback() {
            @Override
            public void success() {
                Pop.popToast(RegisterActivity.this, getString(R.string.register_aty_fetch_vercode_success));
            }

            @Override
            public void error(ACException e) {
                Pop.popToast(RegisterActivity.this, e.getErrorCode() + "-->" + e.getMessage());
            }
        });
    }

    //注册
    public void register() {
        /**
         * @param email
         * @param telephone
         * @param password
         * @param verifyCode
         * @param callback
         */
        accountMgr.register(email, phone, pwd, name, vcode, new PayloadCallback<ACUserInfo>() {
            @Override
            public void success(ACUserInfo userInfo) {
                Pop.popToast(RegisterActivity.this, getString(R.string.register_aty_register_success) + userInfo.toString());
                RegisterActivity.this.finish();
            }

            @Override
            public void error(ACException e) {
                Pop.popToast(RegisterActivity.this, e.getErrorCode() + "-->" + e.getMessage());
            }
        });
    }
}
