package com.holidayjournal.ui.auth;

public interface LoginView {

    void onGoogleLogInSuccess();

    void onError(String message);
}
