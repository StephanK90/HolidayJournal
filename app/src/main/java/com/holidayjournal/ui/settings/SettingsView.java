package com.holidayjournal.ui.settings;

public interface SettingsView {

    void onDataDeleted();

    void onAccountDeleted();

    void onError(String message);
}
