package com.holidayjournal.ui.auth;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Patterns;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Matcher;

class RegisterPresenter {

    private RegisterView mView;

    RegisterPresenter(RegisterView view) {
        this.mView = view;
    }

    void registerUser(String email, String password, String confirmPassword) {
        if (!validCredentials(email, password, confirmPassword)) {
            return;
        }

        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            mView.onRegisterSuccess();

                        } else {
                            mView.onError("Failed to register.");
                        }
                    }
                });
    }

    private boolean validCredentials(String email, String password, String confirmPassword) {
        Matcher matcher = Patterns.EMAIL_ADDRESS.matcher(email);

        if (TextUtils.isEmpty(email)) {
            mView.onError("Enter email address!");
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            mView.onError("Enter password!");
            return false;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            mView.onError("Confirm password!");
            return false;
        }

        if (!matcher.matches()) {
            mView.onError("Invalid email address!");
            return false;
        }

        if (password.length() < 6) {
            mView.onError("Password requires at least 6 characters!");
            return false;
        }

        if (!confirmPassword.equals(password)) {
            mView.onError("Passwords do not match!");
            return false;
        }

        return true;
    }

}
