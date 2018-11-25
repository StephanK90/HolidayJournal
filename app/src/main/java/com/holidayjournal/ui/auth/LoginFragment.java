package com.holidayjournal.ui.auth;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.holidayjournal.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class LoginFragment extends Fragment implements LoginFragmentView, View.OnClickListener {

    @BindView(R.id.login_email)
    EditText mEmail;

    @BindView(R.id.login_pw)
    EditText mPassword;

    @BindView(R.id.login_btn)
    Button mLogin;

    @BindView(R.id.login_pw_reset_btn)
    Button mResetPw;

    @BindView(R.id.login_register_btn)
    Button mRegister;

    private Unbinder unbinder;
    private LoginListener mCallback;
    private LoginFragmentPresenter mPresenter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mPresenter = new LoginFragmentPresenter(this);

        mLogin.setOnClickListener(this);
        mResetPw.setOnClickListener(this);
        mRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_btn:
                mPresenter.validateCredentials(mEmail.getText().toString().trim(), mPassword.getText().toString().trim());
                break;
            case R.id.login_pw_reset_btn:
                mCallback.startResetPassword();
                break;
            case R.id.login_register_btn:
                mCallback.startRegister();
                break;
        }
    }

    @Override
    public void onValidateSuccess() {
        mCallback.logIn(mEmail.getText().toString().trim(), mPassword.getText().toString().trim());
    }

    @Override
    public void onError(String message) {
        mCallback.logInError(message);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallback = (LoginListener) context;
    }

    public interface LoginListener {

        void logIn(String email, String password);

        void logInError(String message);

        void startResetPassword();

        void startRegister();
    }
}
