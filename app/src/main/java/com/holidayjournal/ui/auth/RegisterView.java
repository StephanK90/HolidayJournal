package com.holidayjournal.ui.auth;

public interface RegisterView {

    void onValidateSuccess(String email, String password);

    void onError(String message);
}
