package com.holidayjournal.ui.auth.email;

public interface EmailLoginView {

    void onLogInSuccess();

    void onError(String message);
}
