package com.holidayjournal.ui.settings;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException;
import com.google.firebase.auth.FirebaseUser;

class SettingsFragmentPresenter {

    private SettingsView mView;

    SettingsFragmentPresenter(SettingsView view) {
        this.mView = view;
    }

    void deleteUserAccount() {
        if (getUser() == null) {
            return;
        }

        getUser().delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            mView.onAccountDeleted();
                        } else {
                            if (task.getException() != null) {
                                if (task.getException() instanceof FirebaseAuthRecentLoginRequiredException) {
                                    Log.d("delete_account", "User must re-authenticate.");
                                }
                            }
                        }
                    }
                });
    }

    private void authenticateUser() {
        AuthCredential credential = EmailAuthProvider
                .getCredential("user@example.com", "password1234");

        // Prompt the user to re-provide their sign-in credentials
        getUser().reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        deleteUserAccount();
                    }
                });
    }

    private FirebaseUser getUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }
}
