package com.holidayjournal.ui.auth.register;

interface RegisterView {

    void onValidateSuccess(String email, String password);

    void onValidateError(String message);
}
