package ablecloud.matrix.app.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.accloud.cloudservice.AC;
import com.accloud.cloudservice.PayloadCallback;
import com.accloud.service.ACException;
import com.accloud.service.ACUserInfo;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import java.util.List;

import ablecloud.matrix.app.Constants;
import ablecloud.support.util.UiUtils;
import ablecloud.support.util.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTextChanged;

/**
 * Created by wangkun on 14/04/2017.
 */

public class LoginActivity extends Activity implements Validator.ValidationListener {
    private static final String TAG = "LoginActivity";

    @BindView(R.id.account_layout)
    TextInputLayout mAccountLayout;

    @BindView(R.id.account)
    @NotEmpty(messageResId = R.string.error_field_required)
    EditText mAccount;

    @BindView(R.id.password_layout)
    TextInputLayout mPasswordLayout;

    @BindView(R.id.password)
    @NotEmpty(messageResId = R.string.error_field_required)
    EditText mPassword;

    @BindView(R.id.sign_in_button)
    Button mSigInButton;

    private Validator mInputValidator;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        mInputValidator = new Validator(this);
        mInputValidator.setValidationListener(this);

        mPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == R.id.login || actionId == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        mSigInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });
    }

    private void attemptLogin() {
        mInputValidator.validate();
    }

    @Override
    public void onValidationSucceeded() {
        UiUtils.showProgressDialog(this, true);
        AC.accountMgr().login(mAccount.getText().toString(), mPassword.getText().toString(), new PayloadCallback<ACUserInfo>() {
            @Override
            public void success(ACUserInfo acUserInfo) {
                UiUtils.showProgressDialog(LoginActivity.this, false);
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            }

            @Override
            public void error(ACException e) {
                Log.d(TAG, "login " + e.getMessage());
                UiUtils.showProgressDialog(LoginActivity.this, false);
                switch (e.getErrorCode()) {
                    case Constants.ACCOUNT_NOT_FOUND:
                        UiUtils.showToast(LoginActivity.this, R.string.account_not_found);
                        return;
                    case Constants.ACCOUNT_AUTHENTICATION_ERROR:
                        UiUtils.showToast(LoginActivity.this, R.string.account_authentication_error);
                        return;
                }
                Utils.showError(LoginActivity.this, e);
            }
        });
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        ValidationError validationError = errors.get(0);
        View view = validationError.getView();
        String message = validationError.getCollatedErrorMessage(this);
        if (view == mAccount) {
            mAccountLayout.setError(message);
        } else if (view == mPassword) {
            mPasswordLayout.setError(message);
        }
    }

    @OnTextChanged(R.id.account)
    public void onAccountChanged() {
        mAccountLayout.setError(null);
    }

    @OnTextChanged(R.id.password)
    public void onPasswordChanged() {
        mPasswordLayout.setError(null);
    }
}
