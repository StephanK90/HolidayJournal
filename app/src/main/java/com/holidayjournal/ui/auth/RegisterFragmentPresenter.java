package com.holidayjournal.ui.auth;

import android.text.TextUtils;
import android.util.Patterns;

import java.util.regex.Matcher;

class RegisterFragmentPresenter {

    private RegisterFragmentView mView;

    RegisterFragmentPresenter(RegisterFragmentView view) {
        this.mView = view;
    }

    void validateCredentials(String email, String password) {
        Matcher matcher = Patterns.EMAIL_ADDRESS.matcher(email);

        if (TextUtils.isEmpty(email)) {
            mView.onError("Enter email address!");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            mView.onError("Enter password!");
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

        mView.onValidateSuccess(email, password);
    }
}
