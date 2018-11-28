package com.holidayjournal.ui.auth.login;

public interface LoginView {

    void onGoogleLogInSuccess();

    void onError(String message);
}
