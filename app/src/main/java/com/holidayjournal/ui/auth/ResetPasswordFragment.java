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

public class ResetPasswordFragment extends BaseFragment implements View.OnClickListener, ResetPasswordView {

    @BindView(R.id.reset_pw_email)
    EditText mEmail;

    @BindView(R.id.reset_pw_btn)
    Button mResetBtn;

    private ResetPasswordListener mCallback;
    private ResetPasswordPresenter mPresenter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected int getLayoutID() {
        return R.layout.fragment_reset_password;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mPresenter = new ResetPasswordPresenter(this);

        mResetBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.reset_pw_btn:
                mPresenter.sendPasswordResetEmail(mEmail.getText().toString().trim());
                break;
        }
    }

    @Override
    public void onSuccess() {
        mCallback.emailSent();
    }

    @Override
    public void onError(String message) {
        mCallback.resetPasswordError(message);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallback = (ResetPasswordListener) context;
    }

    public interface ResetPasswordListener {

        void emailSent();

        void resetPasswordError(String message);
    }
}
