package com.holidayjournal.ui.auth;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.holidayjournal.R;
import com.holidayjournal.ui.base.BaseFragment;

import butterknife.BindView;

public class EmailLoginFragment extends BaseFragment implements EmailLoginView, View.OnClickListener {

    @BindView(R.id.email_login_progress)
    ProgressBar mProgressBar;

    @BindView(R.id.login_email)
    EditText mEmail;

    @BindView(R.id.login_pw)
    EditText mPassword;

    @BindView(R.id.login_btn)
    Button mLogin;

    @BindView(R.id.login_pw_reset_btn)
    Button mResetPw;

    private EmailLoginListener mCallback;
    private EmailLoginPresenter mPresenter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected int getLayoutID() {
        return R.layout.fragment_email_login;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mPresenter = new EmailLoginPresenter(this);

        mLogin.setOnClickListener(this);
        mResetPw.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_btn:
                showProgressBar();
                mPresenter.logIn(mEmail.getText().toString().trim(), mPassword.getText().toString().trim());
                break;
            case R.id.login_pw_reset_btn:
                mCallback.startResetPassword();
                break;
        }
    }

    @Override
    public void onLogInSuccess() {
        hideProgressBar();
        mCallback.onEmailLogIn();
    }

    @Override
    public void onError(String message) {
        hideProgressBar();
        mCallback.onEmailLogInError(message);
    }

    private void showProgressBar() {
        if (!mProgressBar.isShown()) {
            if (getActivity() != null) {
                getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
            mProgressBar.setVisibility(View.VISIBLE);
        }
    }

    private void hideProgressBar() {
        if (mProgressBar.isShown()) {
            if (getActivity() != null) {
                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
            mProgressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallback = (EmailLoginListener) context;
    }

    public interface EmailLoginListener {

        void onEmailLogIn();

        void onEmailLogInError(String message);

        void startResetPassword();
    }
}
