package com.holidayjournal.ui.auth;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Patterns;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Matcher;

class EmailLoginPresenter {

    private EmailLoginView mView;

    EmailLoginPresenter(EmailLoginView view) {
        this.mView = view;
    }

    void logIn(String email, String pw) {
        if (!validCredentials(email, pw)) {
            return;
        }
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(email, pw)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            mView.onLogInSuccess();
                        } else {
                            mView.onError("Invalid credentials.");
                        }
                    }
                });
    }

    private boolean validCredentials(String email, String password) {
        Matcher matcher = Patterns.EMAIL_ADDRESS.matcher(email);

        if (TextUtils.isEmpty(email)) {
            mView.onError("Enter email address!");
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            mView.onError("Enter password!");
            return false;
        }

        if (!matcher.matches()) {
            mView.onError("Invalid email address!");
            return false;
        }

        if (password.length() < 6) {
            mView.onError("Password incorrect!");
            return false;
        }

        return true;
    }
}
