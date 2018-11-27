package com.holidayjournal.ui.auth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.Task;
import com.holidayjournal.R;
import com.holidayjournal.ui.base.BaseFragment;
import com.holidayjournal.utils.Utils;

import butterknife.BindView;

public class LoginFragment extends BaseFragment implements LoginView, View.OnClickListener {

    private final int RC_SIGN_IN = 0;

    @BindView(R.id.login_progress)
    ProgressBar mProgressBar;

    @BindView(R.id.login_email)
    EditText mEmail;

    @BindView(R.id.login_pw)
    EditText mPassword;

    @BindView(R.id.login_btn)
    Button mLogin;

    @BindView(R.id.google_sign_in_btn)
    SignInButton mGoogleSignInBtn;

    @BindView(R.id.login_pw_reset_btn)
    Button mResetPw;

    @BindView(R.id.login_register_btn)
    Button mRegister;

    private Context mContext;
    private LoginListener mCallback;
    private LoginPresenter mPresenter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected int getLayoutID() {
        return R.layout.fragment_login;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mPresenter = new LoginPresenter(this);

        mLogin.setOnClickListener(this);
        mResetPw.setOnClickListener(this);
        mRegister.setOnClickListener(this);
        mGoogleSignInBtn.setSize(SignInButton.SIZE_WIDE);
        mGoogleSignInBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_btn:
                showProgressBar();
                mPresenter.logIn(mEmail.getText().toString().trim(), mPassword.getText().toString().trim());
                break;
            case R.id.google_sign_in_btn:
                signInWithGoogle();
                break;
            case R.id.login_pw_reset_btn:
                mCallback.startResetPassword();
                break;
            case R.id.login_register_btn:
                mCallback.startRegister();
                break;
        }
    }

    private void signInWithGoogle() {
        Intent signInIntent = Utils.getGoogleSignInClient(mContext).getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onLogInSuccess() {
        hideProgressBar();
        mCallback.onLogIn();
    }

    @Override
    public void onError(String message) {
        hideProgressBar();
        mCallback.onLogInError(message);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == RC_SIGN_IN) {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                showProgressBar();
                mPresenter.onGoogleSignInResult(task);
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
        mCallback = (LoginListener) context;
    }

    public interface LoginListener {

        void onLogIn();

        void onLogInError(String message);

        void startResetPassword();

        void startRegister();
    }
}
