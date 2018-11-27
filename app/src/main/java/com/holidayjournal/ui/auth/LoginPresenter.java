package com.holidayjournal.ui.auth;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Patterns;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.regex.Matcher;

class LoginPresenter {

    private LoginView mView;
    private FirebaseAuth mAuth;

    LoginPresenter(LoginView view) {
        this.mView = view;
        this.mAuth = FirebaseAuth.getInstance();
    }

    void logIn(String email, String pw) {
        if (!validCredentials(email, pw)) {
            return;
        }

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

    void onGoogleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            if (account != null) {
                firebaseAuthWithGoogle(account);
            }
        } catch (ApiException e) {
            e.printStackTrace();
            mView.onError("Failed to sign in with Google.");
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            mView.onLogInSuccess();
                        } else {
                            mView.onError("Failed to authenticate with Google.");
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
