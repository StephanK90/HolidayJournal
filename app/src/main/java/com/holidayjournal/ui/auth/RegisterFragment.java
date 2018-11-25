package com.holidayjournal.ui.auth;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.holidayjournal.R;
import com.holidayjournal.ui.base.BaseFragment;

import butterknife.BindView;

public class RegisterFragment extends BaseFragment implements View.OnClickListener, RegisterView {

    @BindView(R.id.register_email)
    EditText mEmail;

    @BindView(R.id.register_pw)
    EditText mPass;

    @BindView(R.id.register_confirm_pw)
    EditText mConfirmPw;

    @BindView(R.id.register_btn)
    Button mRegister;

    private RegisterPresenter mPresenter;
    private RegisterListener mCallback;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected int getLayoutID() {
        return R.layout.fragment_register;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mPresenter = new RegisterPresenter(this);

        mRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register_btn:
                mPresenter.validateCredentials(
                        mEmail.getText().toString().trim(),
                        mPass.getText().toString().trim(),
                        mConfirmPw.getText().toString().trim());
                break;
        }
    }

    @Override
    public void onValidateSuccess(String email, String password) {
        mCallback.registerUser(email, password);
    }

    @Override
    public void onError(String message) {
        mCallback.registerError(message);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallback = (RegisterListener) context;
    }

    public interface RegisterListener {

        void registerUser(String email, String password);

        void registerError(String message);
    }
}
