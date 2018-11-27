package com.holidayjournal.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.holidayjournal.R;
import com.holidayjournal.ui.base.BaseActivity;
import com.holidayjournal.ui.holidays.HolidayActivity;

public class AuthActivity extends BaseActivity implements LoginFragment.LoginListener, RegisterFragment.RegisterListener, ResetPasswordFragment.ResetPasswordListener {

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
            onLogIn();
        }
    }

    @Override
    public void onLogIn() {
        showToast("Authentication successful!");
        startHolidayActivity();
    }

    @Override
    public void onLogInError(String message) {
        showToast(message);
    }

    @Override
    public void startRegister() {
        openNewFragment(new RegisterFragment());
    }

    @Override
    public void onUserRegistered() {
        showToast("Register successful!");
        startHolidayActivity();
    }

    @Override
    public void registerError(String message) {
        showToast(message);
    }

    @Override
    public void startResetPassword() {
        openNewFragment(new ResetPasswordFragment());
    }

    @Override
    public void resetPasswordError(String message) {
        showToast(message);
    }

    @Override
    public void emailSent() {
        showToast("Email has been sent!");
        onBackPressed();
    }

    private void openNewFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.auth_layout, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void startHolidayActivity() {
        Intent intent = new Intent(this, HolidayActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

}
