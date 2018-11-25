package com.holidayjournal.ui.auth;

public interface RegisterFragmentView {

    void onValidateSuccess(String email, String password);

    void onError(String message);
}
