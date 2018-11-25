package com.holidayjournal.ui.auth.register;

import android.text.TextUtils;
import android.util.Patterns;

import java.util.regex.Matcher;

class RegisterPresenter {

    private RegisterView mView;

    RegisterPresenter(RegisterView view) {
        this.mView = view;
    }

    void validate(String email, String password) {
        Matcher matcher = Patterns.EMAIL_ADDRESS.matcher(email);

        if (TextUtils.isEmpty(email)) {
            mView.onValidateError("Enter email address!");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            mView.onValidateError("Enter password!");
            return;
        }

        if (!matcher.matches()) {
            mView.onValidateError("Invalid email address!");
            return;
        }

        if (password.length() < 6) {
            mView.onValidateError("Password requires at least 6 characters!");
            return;
        }

        mView.onValidateSuccess(email, password);
    }
}
