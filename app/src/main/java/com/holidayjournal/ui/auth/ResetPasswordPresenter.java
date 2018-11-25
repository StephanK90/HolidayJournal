package com.holidayjournal.ui.auth;

import android.support.annotation.NonNull;
import android.util.Patterns;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Matcher;

class ResetPasswordPresenter {

    private ResetPasswordView mView;

    ResetPasswordPresenter(ResetPasswordView view) {
        this.mView = view;
    }

    void sendPasswordResetEmail(String email) {
        if (!validEmail(email)) {
            mView.onError("Invalid email address!");
            return;
        }

        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            mView.onSuccess();
                        } else {
                            mView.onError("Unknown email address.");
                        }
                    }
                });
    }

    private boolean validEmail(String email) {
        Matcher matcher = Patterns.EMAIL_ADDRESS.matcher(email);
        return matcher.matches();
    }
}
