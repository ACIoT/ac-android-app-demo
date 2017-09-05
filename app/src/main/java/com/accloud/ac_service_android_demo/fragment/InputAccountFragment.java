package com.accloud.ac_service_android_demo.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.accloud.ac_service_android_demo.R;
import com.accloud.ac_service_android_demo.utils.FragmentUtil;
import com.accloud.ac_service_android_demo.utils.ToastUtil;
import com.accloud.cloudservice.AC;
import com.accloud.cloudservice.PayloadCallback;
import com.accloud.cloudservice.VoidCallback;
import com.accloud.service.ACException;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by liuxiaofeng on 05/09/2017.
 */

public class InputAccountFragment extends Fragment {

    @BindView(R.id.phone)
    @NotEmpty(message = "手机号不能为空")
    EditText phone;

    public static final String TAG = "InputAccount";
    Unbinder unbinder;
    Validator validator;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_input_account, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        validator = new Validator(this);
        validator.setValidationListener(validationListener);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.next)
    public void onViewClicked() {
        validator.validate();
    }

    private Validator.ValidationListener validationListener = new Validator.ValidationListener() {
        @Override
        public void onValidationSucceeded() {
            checkExist();
        }

        @Override
        public void onValidationFailed(List<ValidationError> errors) {
            String errorMessage = errors.get(0).getCollatedErrorMessage(getActivity());
            ToastUtil.show(getActivity(), errorMessage.split("\n")[0]);
        }
    };

    private void checkExist() {
        AC.accountMgr().checkExist(phone.getText().toString().trim(), new PayloadCallback<Boolean>() {
            @Override
            public void success(Boolean exist) {
                handleExist(exist);
            }

            @Override
            public void error(ACException e) {
                ToastUtil.show(getActivity(), e.toString());
            }
        });
    }

    private void handleExist(Boolean exist) {
        if (exist) {
            ToastUtil.show(getActivity(), "用户已存在");
        } else {
            askVerifyCode();
        }
    }

    private void askVerifyCode() {
        AC.accountMgr().sendVerifyCode(phone.getText().toString().trim(), 1, new VoidCallback() {
            @Override
            public void success() {
                Bundle arguments = new Bundle();
                arguments.putString("phone", phone.getText().toString().trim());
                SmsVerifyFragment smsVerifyFragment = new SmsVerifyFragment();
                smsVerifyFragment.setArguments(arguments);
                FragmentUtil.replaceSupportFragment((AppCompatActivity) getActivity(), R.id.container, smsVerifyFragment, SmsVerifyFragment.TAG, true, true);
            }

            @Override
            public void error(ACException e) {
                ToastUtil.show(getActivity(), e.toString());
            }
        });
    }
}
