package com.holidayjournal.ui.auth;

import android.text.TextUtils;
import android.util.Patterns;

import java.util.regex.Matcher;

class RegisterPresenter {

    private RegisterView mView;

    RegisterPresenter(RegisterView view) {
        this.mView = view;
    }

    void validateCredentials(String email, String password, String confirmPassword) {
        Matcher matcher = Patterns.EMAIL_ADDRESS.matcher(email);

        if (TextUtils.isEmpty(email)) {
            mView.onError("Enter email address!");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            mView.onError("Enter password!");
            return;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            mView.onError("Confirm password!");
            return;
        }

        if (!matcher.matches()) {
            mView.onError("Invalid email address!");
            return;
        }

        if (password.length() < 6) {
            mView.onError("Password requires at least 6 characters!");
            return;
        }

        if (!confirmPassword.equals(password)) {
            mView.onError("Passwords do not match!");
            return;
        }

        mView.onValidateSuccess(email, password);
    }
}
