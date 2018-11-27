package com.holidayjournal.ui.settings;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException;
import com.holidayjournal.utils.Utils;

class SettingsPresenter {

    private SettingsView mView;

    SettingsPresenter(SettingsView view) {
        this.mView = view;
    }

    void deleteUserAccount() {
        Utils.getUser().delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            mView.onAccountDeleted();
                        } else {
                            if (task.getException() != null) {
                                if (task.getException() instanceof FirebaseAuthRecentLoginRequiredException) {
                                    // ask user to re-authenticate
                                }
                            }
                        }
                    }
                });
    }

    private void reAuthenticateUser(String email, String password) {
        AuthCredential credential = EmailAuthProvider.getCredential(email, password);
        Utils.getUser().reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        deleteUserAccount();
                    }
                });
    }

}
