package com.holidayjournal.ui.auth.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.holidayjournal.R;
import com.holidayjournal.ui.auth.register.RegisterActivity;
import com.holidayjournal.ui.base.BaseActivity;
import com.holidayjournal.ui.holidays.HolidayActivity;

import butterknife.BindView;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

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

    @BindView(R.id.login_progress)
    ProgressBar mProgressBar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        mLogin.setOnClickListener(this);
        mResetPw.setOnClickListener(this);
        mRegister.setOnClickListener(this);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_login;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            loginSuccessful();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_btn:
                logIn();
                break;
            case R.id.login_pw_reset_btn:
                break;
            case R.id.login_register_btn:
                Intent register = new Intent(this, RegisterActivity.class);
                startActivity(register);
                break;
        }
    }

    private void logIn() {
        showProgressBar();
        String email = mEmail.getText().toString().trim();
        String pw = mPassword.getText().toString().trim();

        mAuth.signInWithEmailAndPassword(email, pw)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            loginSuccessful();
                        } else {
                            showToast("Invalid credentials.");
                        }
                        hideProgressBar();
                    }
                });
    }

    private void loginSuccessful() {
        showToast("Authentication successful!");
        Intent intent = new Intent(this, HolidayActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void showProgressBar() {
        if (!mProgressBar.isShown()) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            mProgressBar.setVisibility(View.VISIBLE);
        }
    }

    private void hideProgressBar() {
        if (mProgressBar.isShown()) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            mProgressBar.setVisibility(View.GONE);
        }
    }

}
