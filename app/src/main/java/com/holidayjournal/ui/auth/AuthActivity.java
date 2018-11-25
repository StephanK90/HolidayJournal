package com.holidayjournal.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.holidayjournal.R;
import com.holidayjournal.ui.base.BaseActivity;
import com.holidayjournal.ui.holidays.HolidayActivity;

import butterknife.BindView;

public class AuthActivity extends BaseActivity implements LoginFragment.LoginListener, RegisterFragment.RegisterListener, ResetPasswordFragment.ResetPasswordListener {

    @BindView(R.id.login_progress)
    ProgressBar mProgressBar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.auth_layout, new LoginFragment());
        transaction.commit();
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_auth;
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
    public void logIn(String email, String pw) {
        showProgressBar();
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
    }

    @Override
    public void logInError(String message) {
        showToast(message);
    }

    @Override
    public void startRegister() {
        openFragment(new RegisterFragment());
    }

    @Override
    public void registerUser(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            registerSuccessful();

                        } else {
                            showToast("Failed to register.");
                        }
                    }
                });
    }

    private void registerSuccessful() {
        showToast("Register successful!");
        Intent intent = new Intent(this, HolidayActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void registerError(String message) {
        showToast(message);
    }

    @Override
    public void startResetPassword() {
        openFragment(new ResetPasswordFragment());
    }

    @Override
    public void emailSent() {
        showToast("Email has been sent!");
        onBackPressed();
    }

    @Override
    public void resetPasswordError(String message) {
        showToast(message);
    }

    private void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.auth_layout, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
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
